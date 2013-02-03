:-lib(ic).
:-lib(ic_global).
:-lib(ic_global_gac).
:-lib(apply).
:-lib(apply_macros).
:-lib(lists).
:-lib(ech).
:- local struct(schema(oid)).
:- local struct(entity(oid,name)).
:- local struct(relship(oid,name)).
:- local struct(relshipend(oid,name)).
:- local struct(attribute(oid,name,isKey)).
:- local struct(entities_schema(schema,entities)).
:- local struct(relships_schema(schema,relships)).
:- local struct(ends_type(type,ends)).
:- local struct(attrs_entity(entity,attrs)).
:- local struct(ends_relship(relship,ends)).

findSolutions(Instances):-

	%Cardinality definitions
	SSchema::0..5, SEntity::0..5, SRelship::0..5, SRelshipEnd::0..5, SAttribute::0..5, 
	Sentities_schema::0..10, Srelships_schema::0..10, Sends_type::0..10, Sattrs_entity::0..10, Sends_relship::0..10, 
	CardVariables=[SSchema, SEntity, SRelship, SRelshipEnd, SAttribute, Sentities_schema, Srelships_schema, Sends_type, Sattrs_entity, Sends_relship],
	
	%Cardinality constraints
	% cardinality constraints derived from containment tree (compositions)
	SEntity #= Sentities_schema,
	SRelship #= Srelships_schema,
	SRelshipEnd #= Sends_relship,
	SAttribute #= Sattrs_entity,
strongSatisfiability(CardVariables),
	
	constraintsentities_schemaCard(CardVariables),
	constraintsrelships_schemaCard(CardVariables),
	constraintsends_typeCard(CardVariables),
	constraintsattrs_entityCard(CardVariables),
	constraintsends_relshipCard(CardVariables),
	
	%Instantiation of cardinality variables
	labeling(CardVariables),
	
	Instances = [OSchema, OEntity, ORelship, ORelshipEnd, OAttribute, Lentities_schema, Lrelships_schema, Lends_type, Lattrs_entity, Lends_relship ],
	
	%Object creation
	creationSchema(OSchema, SSchema, SSchema, AtSchema),
	creationEntity(OEntity, SEntity, SEntity, AtEntity),
	creationRelship(ORelship, SRelship, SRelship, AtRelship),
	creationRelshipEnd(ORelshipEnd, SRelshipEnd, SRelshipEnd, AtRelshipEnd),
	creationAttribute(OAttribute, SAttribute, SAttribute, AtAttribute),
	
	
	differentOids(OSchema),
	differentOids(OEntity),
	differentOids(ORelship),
	differentOids(ORelshipEnd),
	differentOids(OAttribute),
	
	orderedInstances(OSchema),
	orderedInstances(OEntity),
	orderedInstances(ORelship),
	orderedInstances(ORelshipEnd),
	orderedInstances(OAttribute),
	
	%Links creation
	creationentities_schema(Lentities_schema, Sentities_schema, Pentities_schema, SSchema, SEntity),
	creationrelships_schema(Lrelships_schema, Srelships_schema, Prelships_schema, SSchema, SRelship),
	creationends_type(Lends_type, Sends_type, Pends_type, SEntity, SRelshipEnd),
	creationattrs_entity(Lattrs_entity, Sattrs_entity, Pattrs_entity, SEntity, SAttribute),
	creationends_relship(Lends_relship, Sends_relship, Pends_relship, SRelship, SRelshipEnd),
	differentLinks(Lentities_schema),
	differentLinks(Lrelships_schema),
	differentLinks(Lends_type),
	differentLinks(Lattrs_entity),
	differentLinks(Lends_relship),
	orderedLinks(Instances,"entities_schema"),
	orderedLinks(Instances,"relships_schema"),
	orderedLinks(Instances,"ends_type"),
	orderedLinks(Instances,"attrs_entity"),
	orderedLinks(Instances,"ends_relship"),
	
	cardinalityLinksentities_schema(Instances),
	cardinalityLinksrelships_schema(Instances),
	cardinalityLinksends_type(Instances),
	cardinalityLinksattrs_entity(Instances),
	cardinalityLinksends_relship(Instances),
	
eRN(Instances),
	rN(Instances),
	eN(Instances),
	eAN(Instances),
	kEY(Instances),
	rEN(Instances),
	
	AllAttributes = [Pentities_schema, Prelships_schema, Pends_type, Pattrs_entity, Pends_relship, AtSchema, AtEntity, AtRelship, AtRelshipEnd, AtAttribute ],
	flatten(AllAttributes, Attributes),
	labeling(Attributes),
	
str_labeling.

index("Schema",1).
index("Entity",2).
index("Relship",3).
index("RelshipEnd",4).
index("Attribute",5).
index("entities_schema",6).
index("relships_schema",7).
index("ends_type",8).
index("attrs_entity",9).
index("ends_relship",10).
attIndex("Entity","name",2).
attIndex("Relship","name",2).
attIndex("RelshipEnd","name",2).
attIndex("Attribute","name",2).
attIndex("Attribute","isKey",3).
attType("Entity","name","EBigInteger").
attType("Relship","name","EBigInteger").
attType("RelshipEnd","name","EBigInteger").
attType("Attribute","name","EBigInteger").
attType("Attribute","isKey","EBoolean").

roleIndex("entities_schema","schema",1).
roleIndex("entities_schema","entities",2).
roleIndex("relships_schema","schema",1).
roleIndex("relships_schema","relships",2).
roleIndex("ends_type","type",1).
roleIndex("ends_type","ends",2).
roleIndex("attrs_entity","entity",1).
roleIndex("attrs_entity","attrs",2).
roleIndex("ends_relship","relship",1).
roleIndex("ends_relship","ends",2).
roleType("entities_schema","schema","Schema").
roleType("entities_schema","entities","Entity").
roleType("relships_schema","schema","Schema").
roleType("relships_schema","relships","Relship").
roleType("ends_type","type","Entity").
roleType("ends_type","ends","RelshipEnd").
roleType("attrs_entity","entity","Entity").
roleType("attrs_entity","attrs","Attribute").
roleType("ends_relship","relship","Relship").
roleType("ends_relship","ends","RelshipEnd").
roleMin("entities_schema","schema",1).
roleMin("entities_schema","entities",1).
roleMin("relships_schema","schema",1).
roleMin("relships_schema","relships",0).
roleMin("ends_type","type",1).
roleMin("ends_type","ends",0).
roleMin("attrs_entity","entity",1).
roleMin("attrs_entity","attrs",1).
roleMin("ends_relship","relship",1).
roleMin("ends_relship","ends",2).
roleMax("entities_schema","schema",1).
roleMax("entities_schema","entities","*").
roleMax("relships_schema","schema",1).
roleMax("relships_schema","relships","*").
roleMax("ends_type","type",1).
roleMax("ends_type","ends","*").
roleMax("attrs_entity","entity",1).
roleMax("attrs_entity","attrs","*").
roleMax("ends_relship","relship",1).
roleMax("ends_relship","ends","*").

assocIsUnique("entities_schema", 1).
assocIsUnique("relships_schema", 1).
assocIsUnique("ends_type", 1).
assocIsUnique("attrs_entity", 1).
assocIsUnique("ends_relship", 1).


strongSatisfiability(CardVariables):- strongSatisfiabilityConstraint(CardVariables).

constraintsentities_schemaCard(CardVariables):-constraintsBinAssocMultiplicities("entities_schema", "schema", "entities", CardVariables).
constraintsrelships_schemaCard(CardVariables):-constraintsBinAssocMultiplicities("relships_schema", "schema", "relships", CardVariables).
constraintsends_typeCard(CardVariables):-constraintsBinAssocMultiplicities("ends_type", "type", "ends", CardVariables).
constraintsattrs_entityCard(CardVariables):-constraintsBinAssocMultiplicities("attrs_entity", "entity", "attrs", CardVariables).
constraintsends_relshipCard(CardVariables):-constraintsBinAssocMultiplicities("ends_relship", "relship", "ends", CardVariables).

creationSchema(Instances, Size, _, Attributes):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), for(N, 1, Size) do
		Xi=schema{oid:N}, append([N],AtIn, AtOut)).

creationEntity(Instances, Size, _, Attributes):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), for(N, 1, Size) do
		Xi=entity{oid:N,name:Int2}, Int2#::[1,10,20],
		 append([N,Int2],AtIn, AtOut)).

creationRelship(Instances, Size, _, Attributes):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), for(N, 1, Size) do
		Xi=relship{oid:N,name:Int2}, Int2#::[1,10,20],
		 append([N,Int2],AtIn, AtOut)).

creationRelshipEnd(Instances, Size, _, Attributes):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), for(N, 1, Size) do
		Xi=relshipend{oid:N,name:Int2}, Int2#::[1,10,20],
		 append([N,Int2],AtIn, AtOut)).

creationAttribute(Instances, Size, _, Attributes):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), for(N, 1, Size) do
		Xi=attribute{oid:N,name:Int2,isKey:Int3}, Int2#::[1,10,20],
		 Int3#::0..1,
		 append([N,Int2,Int3],AtIn, AtOut)).


creationentities_schema(Instances, Size, Participants, SSchema, SEntity):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants), param(SSchema), param(SEntity) do
		Xi=entities_schema{schema:ValuePart1,entities:ValuePart2}, ValuePart1#>0, ValuePart1#=<SSchema, ValuePart2#>0, ValuePart2#=<SEntity,
		append([ValuePart1, ValuePart2],AtIn, AtOut)).
creationrelships_schema(Instances, Size, Participants, SSchema, SRelship):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants), param(SSchema), param(SRelship) do
		Xi=relships_schema{schema:ValuePart1,relships:ValuePart2}, ValuePart1#>0, ValuePart1#=<SSchema, ValuePart2#>0, ValuePart2#=<SRelship,
		append([ValuePart1, ValuePart2],AtIn, AtOut)).
creationends_type(Instances, Size, Participants, SEntity, SRelshipEnd):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants), param(SEntity), param(SRelshipEnd) do
		Xi=ends_type{type:ValuePart1,ends:ValuePart2}, ValuePart1#>0, ValuePart1#=<SEntity, ValuePart2#>0, ValuePart2#=<SRelshipEnd,
		append([ValuePart1, ValuePart2],AtIn, AtOut)).
creationattrs_entity(Instances, Size, Participants, SEntity, SAttribute):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants), param(SEntity), param(SAttribute) do
		Xi=attrs_entity{entity:ValuePart1,attrs:ValuePart2}, ValuePart1#>0, ValuePart1#=<SEntity, ValuePart2#>0, ValuePart2#=<SAttribute,
		append([ValuePart1, ValuePart2],AtIn, AtOut)).
creationends_relship(Instances, Size, Participants, SRelship, SRelshipEnd):-
	length(Instances, Size),
	(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants), param(SRelship), param(SRelshipEnd) do
		Xi=ends_relship{relship:ValuePart1,ends:ValuePart2}, ValuePart1#>0, ValuePart1#=<SRelship, ValuePart2#>0, ValuePart2#=<SRelshipEnd,
		append([ValuePart1, ValuePart2],AtIn, AtOut)).
cardinalityLinksentities_schema(Instances):-
	linksConstraintMultiplicities(Instances, "entities_schema","schema","entities").
cardinalityLinksrelships_schema(Instances):-
	linksConstraintMultiplicities(Instances, "relships_schema","schema","relships").
cardinalityLinksends_type(Instances):-
	linksConstraintMultiplicities(Instances, "ends_type","type","ends").
cardinalityLinksattrs_entity(Instances):-
	linksConstraintMultiplicities(Instances, "attrs_entity","entity","attrs").
cardinalityLinksends_relship(Instances):-
	linksConstraintMultiplicities(Instances, "ends_relship","relship","ends").

% OCL constraint ER::Schema.allInstances()->forAll(self : Schema | self.entities->forAll(e : Entity | self.relships->forAll(r : Relship | e.name.<>(r.name))))
nallInstances1ERN(Instances, _, Result):-
	ocl_allInstances(Instances, "Schema", Result).
% Lookup for variable self
nVariable2ERN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nNavigation3ERN(Instances, Vars, Result):-
	nVariable2ERN(Instances, Vars, Value1),
	ocl_navigation(Instances,"entities_schema","schema","entities", Value1, Result).
% Lookup for variable self
nVariable4ERN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nNavigation5ERN(Instances, Vars, Result):-
	nVariable4ERN(Instances, Vars, Value1),
	ocl_navigation(Instances,"relships_schema","schema","relships", Value1, Result).
% Lookup for variable e
nVariable6ERN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nAttribute7ERN(Instances, Vars, Result):-
	nVariable6ERN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Entity","name", Object, Result).
% Lookup for variable r
nVariable8ERN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nAttribute9ERN(Instances, Vars, Result):-
	nVariable8ERN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Relship","name", Object, Result).
nnot_equals10ERN(Instances, Vars, Result):-
	ocl_int_not_equals(Instances, Vars, nAttribute7ERN, nAttribute9ERN, Result).
nforAll11ERN(Instances, Vars, Result):-
	nNavigation5ERN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nnot_equals10ERN, Result).
nforAll12ERN(Instances, Vars, Result):-
	nNavigation3ERN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll11ERN, Result).
nforAll13ERN(Instances, Vars, Result):-
	nallInstances1ERN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll12ERN, Result).
eRN(Instances):-
	nforAll13ERN(Instances, [], Result),
	Result #=1.

% OCL constraint ER::Schema.allInstances()->forAll(self : Schema | self.relships->forAll(r1 : Relship, r2 : Relship | r1.name.=(r2.name).implies(r1.=(r2))))
nallInstances1RN(Instances, _, Result):-
	ocl_allInstances(Instances, "Schema", Result).
% Lookup for variable self
nVariable2RN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nNavigation3RN(Instances, Vars, Result):-
	nVariable2RN(Instances, Vars, Value1),
	ocl_navigation(Instances,"relships_schema","schema","relships", Value1, Result).
% Lookup for variable self
nVariable4RN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nNavigation5RN(Instances, Vars, Result):-
	nVariable4RN(Instances, Vars, Value1),
	ocl_navigation(Instances,"relships_schema","schema","relships", Value1, Result).
% Lookup for variable r1
nVariable6RN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nAttribute7RN(Instances, Vars, Result):-
	nVariable6RN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Relship","name", Object, Result).
% Lookup for variable r2
nVariable8RN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nAttribute9RN(Instances, Vars, Result):-
	nVariable8RN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Relship","name", Object, Result).
nequals10RN(Instances, Vars, Result):-
	ocl_int_equals(Instances, Vars, nAttribute7RN, nAttribute9RN, Result).
% Lookup for variable r1
nVariable11RN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
% Lookup for variable r2
nVariable12RN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nequals13RN(Instances, Vars, Result):-
	nVariable11RN(Instances, Vars, Obj1),
	nVariable12RN(Instances, Vars, Obj2),
	ocl_obj_equals(Instances, Obj1, "VOID", Obj2, "VOID", Result).
nimplies14RN(Instances, Vars, Result):-
	ocl_implies(Instances, Vars, nequals10RN, nequals13RN, Result).
nforAll15RN(Instances, Vars, Result):-
	nNavigation5RN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nimplies14RN, Result).
nforAll16RN(Instances, Vars, Result):-
	nNavigation3RN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll15RN, Result).
nforAll17RN(Instances, Vars, Result):-
	nallInstances1RN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll16RN, Result).
rN(Instances):-
	nforAll17RN(Instances, [], Result),
	Result #=1.

% OCL constraint ER::Schema.allInstances()->forAll(self : Schema | self.entities->forAll(e1 : Entity, e2 : Entity | e1.name.=(e2.name).implies(e1.=(e2))))
nallInstances1EN(Instances, _, Result):-
	ocl_allInstances(Instances, "Schema", Result).
% Lookup for variable self
nVariable2EN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nNavigation3EN(Instances, Vars, Result):-
	nVariable2EN(Instances, Vars, Value1),
	ocl_navigation(Instances,"entities_schema","schema","entities", Value1, Result).
% Lookup for variable self
nVariable4EN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nNavigation5EN(Instances, Vars, Result):-
	nVariable4EN(Instances, Vars, Value1),
	ocl_navigation(Instances,"entities_schema","schema","entities", Value1, Result).
% Lookup for variable e1
nVariable6EN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nAttribute7EN(Instances, Vars, Result):-
	nVariable6EN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Entity","name", Object, Result).
% Lookup for variable e2
nVariable8EN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nAttribute9EN(Instances, Vars, Result):-
	nVariable8EN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Entity","name", Object, Result).
nequals10EN(Instances, Vars, Result):-
	ocl_int_equals(Instances, Vars, nAttribute7EN, nAttribute9EN, Result).
% Lookup for variable e1
nVariable11EN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
% Lookup for variable e2
nVariable12EN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nequals13EN(Instances, Vars, Result):-
	nVariable11EN(Instances, Vars, Obj1),
	nVariable12EN(Instances, Vars, Obj2),
	ocl_obj_equals(Instances, Obj1, "VOID", Obj2, "VOID", Result).
nimplies14EN(Instances, Vars, Result):-
	ocl_implies(Instances, Vars, nequals10EN, nequals13EN, Result).
nforAll15EN(Instances, Vars, Result):-
	nNavigation5EN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nimplies14EN, Result).
nforAll16EN(Instances, Vars, Result):-
	nNavigation3EN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll15EN, Result).
nforAll17EN(Instances, Vars, Result):-
	nallInstances1EN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll16EN, Result).
eN(Instances):-
	nforAll17EN(Instances, [], Result),
	Result #=1.

% OCL constraint ER::Entity.allInstances()->forAll(self : Entity | self.attrs->forAll(a1 : Attribute, a2 : Attribute | a1.name.=(a2.name).implies(a1.=(a2))))
nallInstances1EAN(Instances, _, Result):-
	ocl_allInstances(Instances, "Entity", Result).
% Lookup for variable self
nVariable2EAN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nNavigation3EAN(Instances, Vars, Result):-
	nVariable2EAN(Instances, Vars, Value1),
	ocl_navigation(Instances,"attrs_entity","entity","attrs", Value1, Result).
% Lookup for variable self
nVariable4EAN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nNavigation5EAN(Instances, Vars, Result):-
	nVariable4EAN(Instances, Vars, Value1),
	ocl_navigation(Instances,"attrs_entity","entity","attrs", Value1, Result).
% Lookup for variable a1
nVariable6EAN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nAttribute7EAN(Instances, Vars, Result):-
	nVariable6EAN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Attribute","name", Object, Result).
% Lookup for variable a2
nVariable8EAN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nAttribute9EAN(Instances, Vars, Result):-
	nVariable8EAN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Attribute","name", Object, Result).
nequals10EAN(Instances, Vars, Result):-
	ocl_int_equals(Instances, Vars, nAttribute7EAN, nAttribute9EAN, Result).
% Lookup for variable a1
nVariable11EAN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
% Lookup for variable a2
nVariable12EAN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nequals13EAN(Instances, Vars, Result):-
	nVariable11EAN(Instances, Vars, Obj1),
	nVariable12EAN(Instances, Vars, Obj2),
	ocl_obj_equals(Instances, Obj1, "VOID", Obj2, "VOID", Result).
nimplies14EAN(Instances, Vars, Result):-
	ocl_implies(Instances, Vars, nequals10EAN, nequals13EAN, Result).
nforAll15EAN(Instances, Vars, Result):-
	nNavigation5EAN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nimplies14EAN, Result).
nforAll16EAN(Instances, Vars, Result):-
	nNavigation3EAN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll15EAN, Result).
nforAll17EAN(Instances, Vars, Result):-
	nallInstances1EAN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll16EAN, Result).
eAN(Instances):-
	nforAll17EAN(Instances, [], Result),
	Result #=1.

% OCL constraint ER::Entity.allInstances()->forAll(self : Entity | self.attrs->exists(a : Attribute | a.isKey.=(true)))
nallInstances1KEY(Instances, _, Result):-
	ocl_allInstances(Instances, "Entity", Result).
% Lookup for variable self
nVariable2KEY(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nNavigation3KEY(Instances, Vars, Result):-
	nVariable2KEY(Instances, Vars, Value1),
	ocl_navigation(Instances,"attrs_entity","entity","attrs", Value1, Result).
% Lookup for variable a
nVariable4KEY(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nAttribute5KEY(Instances, Vars, Result):-
	nVariable4KEY(Instances, Vars, Object),
	ocl_attributeCall(Instances,"Attribute","isKey", Object, Result).
nConstant6KEY(_, _, Result):-
	Result=1.
nequals7KEY(Instances, Vars, Result):-
	ocl_boolean_equals(Instances, Vars, nAttribute5KEY, nConstant6KEY, Result).
nexists8KEY(Instances, Vars, Result):-
	nNavigation3KEY(Instances, Vars, Value1),
	ocl_col_exists(Instances, Vars, Value1, nequals7KEY, Result).
nforAll9KEY(Instances, Vars, Result):-
	nallInstances1KEY(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nexists8KEY, Result).
kEY(Instances):-
	nforAll9KEY(Instances, [], Result),
	Result #=1.

% OCL constraint ER::Relship.allInstances()->forAll(self : Relship | self.ends->forAll(e1 : RelshipEnd, e2 : RelshipEnd | e1.name.=(e2.name).implies(e1.=(e2))))
nallInstances1REN(Instances, _, Result):-
	ocl_allInstances(Instances, "Relship", Result).
% Lookup for variable self
nVariable2REN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nNavigation3REN(Instances, Vars, Result):-
	nVariable2REN(Instances, Vars, Value1),
	ocl_navigation(Instances,"ends_relship","relship","ends", Value1, Result).
% Lookup for variable self
nVariable4REN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nNavigation5REN(Instances, Vars, Result):-
	nVariable4REN(Instances, Vars, Value1),
	ocl_navigation(Instances,"ends_relship","relship","ends", Value1, Result).
% Lookup for variable e1
nVariable6REN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
nAttribute7REN(Instances, Vars, Result):-
	nVariable6REN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"RelshipEnd","name", Object, Result).
% Lookup for variable e2
nVariable8REN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nAttribute9REN(Instances, Vars, Result):-
	nVariable8REN(Instances, Vars, Object),
	ocl_attributeCall(Instances,"RelshipEnd","name", Object, Result).
nequals10REN(Instances, Vars, Result):-
	ocl_int_equals(Instances, Vars, nAttribute7REN, nAttribute9REN, Result).
% Lookup for variable e1
nVariable11REN(_, Vars, Result):-
	ocl_variable(Vars,2,Result).
% Lookup for variable e2
nVariable12REN(_, Vars, Result):-
	ocl_variable(Vars,1,Result).
nequals13REN(Instances, Vars, Result):-
	nVariable11REN(Instances, Vars, Obj1),
	nVariable12REN(Instances, Vars, Obj2),
	ocl_obj_equals(Instances, Obj1, "VOID", Obj2, "VOID", Result).
nimplies14REN(Instances, Vars, Result):-
	ocl_implies(Instances, Vars, nequals10REN, nequals13REN, Result).
nforAll15REN(Instances, Vars, Result):-
	nNavigation5REN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nimplies14REN, Result).
nforAll16REN(Instances, Vars, Result):-
	nNavigation3REN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll15REN, Result).
nforAll17REN(Instances, Vars, Result):-
	nallInstances1REN(Instances, Vars, Value1),
	ocl_col_forAll(Instances, Vars, Value1, nforAll16REN, Result).
rEN(Instances):-
	nforAll17REN(Instances, [], Result),
	Result #=1.


