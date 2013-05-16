% 2009/06/09
% - Added functions for inheritance


% 2007/03/21 
% - orderedObjects: additional code should be added to take care of 
%   floating-point attributes
% - decide where orderedObjects, orderedOids and orderedLinks should be called

%
% ECLiPSe libraries
%

:- lib(ic).     % Interval constraints
:- lib(apply).  % Using variables as functors


%------------------------------------------------------------------------------
%
% Utility methods - methods dealing with oids
% 
%------------------------------------------------------------------------------



% getOid(Object, Oid) :-
%    Get the Oid of an Object. It is always the first field of the struct.

getOid(Object, Oid) :- 
   arg(1, Object, Oid).

% getOidList(ObjectList, OidList) :-
%    OidList is the list of all Oids of Objects, where Objects is
%    a list of objects.

getOidList(ObjectList, OidList) :-
  ( foreach(Object, ObjectList),
    foreach(Oid, OidList)
    do
      getOid(Object, Oid)
  ).

% existsOidInList( ?Oid, ?OidList ):-
%    Constrain an Oid to be a member of an OidList with a known length

existsOidInList(Oid, OidList) :-
   ( foreach(Other, OidList),
     fromto(0, In, Out, Result),
     param(Oid)
     do 
       #=(Oid, Other, Aux),
       ic:or(In, Aux, Out)
   ),
   Result #=1.

% countOidInList( ?OidList, ?Oid, ?Result ) :-
%    Counts the number of times that an Oid appears in OidList 
%    The length of the OidList must be known

countOidInList( OidList, Oid, Result ) :-
   ( foreach(Elem, OidList),
     foreach(TruthValue, Found),
     param(Oid)
     do 
       % TruthValue = 1 if the object has been found, 0 otherwise
       #=(Elem, Oid, TruthValue)
   ),
   Result #= sum(Found).

%------------------------------------------------------------------------------
%
% Constraints on classes - uniqueness of oids, ...
% 
%------------------------------------------------------------------------------

%differentOids(Instances) :-
%   Oids of a class must have a different value 
%   Note: this description is kept for clarity, but it is already
%   implied by the stronger "orderedOids" later

differentOids(Instances):-
   getOidList(Instances, OidList),
   ic_global:alldifferent(OidList). 

%orderedInstances(Instances) :-
%   All instances of a class must be assigned in increasing order
%   This restriction is imposed for efficiency reasons to avoid
%   exploring all possible permutations within a class

orderedInstances(Instances) :-
  getOidList(Instances, OidList),
  % Oids should fulfill the #< relation
  orderedList(#<, OidList).
%TODO: need to reconsider orderForAttributes - for various examples, the overhead is to big
% , orderedList(orderForAttributes, Instances).

%orderForAttributes(ObjectA, ObjectB) :-
%   Constrain ObjectA to be less or equal than ObjectB, according to some order.
%   Let at(i,Obj) be the i-th attribute of Obj, then ObjectA is less or equal 
%   than ObjectB if:
%     ( at(1,A) < at(1,B) ) OR
%     ( at(1,A) = at(1,B) AND at(2,A) < at(2,B) ) OR
%     ( at(1,A) = at(1,B) AND at(2,A) = at(2,B) AND at(3,A) < at(3,B) ) OR
%     ...
%     all attributes are equal

orderForAttributes(ObjectA, ObjectB) :-
  ( foreacharg(AttribA, ObjectA), 
    foreacharg(AttribB, ObjectB),
    fromto(0, InOrder, OutOrder, AllLess),
    fromto(1, InEquals, OutEquals, AllEqual),
    count(I,1,_)
    do     
      ( I = 1 -> 
        % Order in Oids is defined differently (see orderedOids)  
        OutEquals = InEquals,
        OutOrder  = InOrder
        ;
        % We should treat use ($<) and ($=) for float attributes 
        % instead of (#<) and (#=)
        #<(AttribA, AttribB, Less),
        #=(AttribA, AttribB, Equals),              
        % InEquals = participants 1 to I-1 are equal
        % OutEquals should extend it up to the I-th participant    
        % OutEquals = InEquals /\ Equals
        ic:and(InEquals, Equals, OutEquals),      
        % InOrder = participants 1 to I-1 are ordered
        % OutOrder should extend it up to the I-th participant
        % OutOrder = InOrder \/ (InEquals /\ Less) 
        ic:and(InEquals, Less, Aux),
        ic:or(InOrder, Aux, OutOrder) )
  ),
  ic:or(AllLess, AllEqual, Result),
  Result #=1.

%------------------------------------------------------------------------------
%
% Associations - multiplicities, uniqueness of links, ...
% 
%------------------------------------------------------------------------------



% constraintsBinAssocMultiplicities(Assoc, RoleA, RoleB, CardList)
%    Define constraints on the participants cardinalities derived from
%    the minimum and maximum  multiplicity of a given association

constraintsBinAssocMultiplicities(Assoc, RoleA, RoleB, CardList) :-
  % Check the value of the "Unique" flag for this association
  assocIsUnique(Assoc, Unique),
  % Get the minimum and maximum cardinalities for both ends
  % of the association
  roleMin(Assoc, RoleA, MinA),
  roleMax(Assoc, RoleA, MaxA),
  roleMin(Assoc, RoleB, MinB),
  roleMax(Assoc, RoleB, MaxB),
  % Get the indices for the associations and both classes
  index(Assoc, AssocIndex),
  roleType(Assoc, RoleA, ClassA),
  index(ClassA, ClassIndexA),
  roleType(Assoc, RoleB, ClassB),
  index(ClassB, ClassIndexB),
  % Get the cardinality variables for the association and both classes
  nth1(AssocIndex, CardList, SizeAssoc),
  nth1(ClassIndexA, CardList, SizeA),
  nth1(ClassIndexB, CardList, SizeB),
  % Multiplicity constraints
  (Unique = 1   -> SizeAssoc #=< SizeA * SizeB; true),
  (MinA  \= 0   -> SizeAssoc #>= MinA * SizeB;  true),
  (MinB  \= 0   -> SizeAssoc #>= MinB * SizeA;  true),
  (MaxA  \= "*" -> SizeAssoc #=< MaxA * SizeB;  true),
  (MaxB  \= "*" -> SizeAssoc #=< MaxB * SizeA;  true).


% constraintsBinAssocMultiplicities(Assoc, RoleA, RoleB, CardList)
%    Define constraints on the participants cardinalities derived from
%    the minimum and maximum  multiplicity of a given association
%
% 03.02.2012 FB: removed suspension on ground(Instances), suspension is now
%                on both link end lists, additionally, global constraints
%                are applied on upper bounds and in the special case [N..N].   
linksConstraintMultiplicities(Instances, Assoc, RoleA, RoleB) :-
  index(Assoc, IndexAssoc),
  nth1(IndexAssoc, Instances, LAssoc),

  % Get the minimum and maximum cardinalities for both ends
  % of the association
  roleMin(Assoc, RoleA, MinA),
  roleMax(Assoc, RoleA, MaxA),
  roleMin(Assoc, RoleB, MinB),
  roleMax(Assoc, RoleB, MaxB),

  % Get the indices for roles
  roleIndex(Assoc, RoleA, RoleIndexA),
  roleIndex(Assoc, RoleB, RoleIndexB),

  % Get the indices for the associations and both classes
  roleType(Assoc, RoleA, ClassA),
  index(ClassA, ClassIndexA),
  roleType(Assoc, RoleB, ClassB),
  index(ClassB, ClassIndexB),

  % Get the instances variables for the association and both classes
  nth1(ClassIndexA, Instances, OClassA),
  nth1(ClassIndexB, Instances, OClassB),
  getPartList(LAssoc, RoleIndexA, LA),
  getPartList(LAssoc, RoleIndexB, LB),
  linksConstraintMultiplicities4(OClassA,MinA,MaxA,LA,OClassB,MinB,MinB,LB),
% lcm1 is subsumed by lcm3
%  linksConstraintMultiplicities1(OClassA,MinB,MaxB,LA),
%  linksConstraintMultiplicities1(OClassB,MinA,MaxA,LB),
  linksConstraintMultiplicities2(MaxB,LA),
  linksConstraintMultiplicities2(MaxA,LB),
  linksConstraintMultiplicities3(OClassA,MinB,MaxB,LA),
  linksConstraintMultiplicities3(OClassB,MinA,MaxA,LB).


% helper for linksConstraintMultiplicities 
% check multiplicities for ground link end list

% obsolete
linksConstraintMultiplicities1(_,0,"*",_) :- !.
linksConstraintMultiplicities1(OClassA,MinB,MaxB,LA) :-
  ground(LA),
  !,
  ( foreach(Obj, OClassA), 
    param(MinB, MaxB, LA) 
    do
      getOid(Obj, Oid),
      countOidInList(LA, Oid, Size),
      Size #>= MinB, 
      (MaxB  \= "*" -> Size #=< MaxB;  true)
  ).
linksConstraintMultiplicities1(OClassA,MinB,MaxB,LA) :-
  % nonground LA
  suspend(linksConstraintMultiplicities1(OClassA,MinB,MaxB,LA),0,LA->bound). 


% helper for linksConstraintMultiplicities 
% pose global constraint on upper bound
linksConstraintMultiplicities2("*",_) :- !.
linksConstraintMultiplicities2(MaxB,LA) :-
	%printf("ic_global:alldifferent(%w,%w)",[LA,MaxB]),nl,
	ic_global:alldifferent(LA,MaxB).



%% % helper for linksConstraintMultiplicities 
%% % pose global constraint for the special case N..N 

%% linksConstraintMultiplicities3(OClassA,1,1,LA) :-
%%   !,
%%   getOidList(OClassA,OidList),
%%   ic_global:sorted(LA,OidList).
  
linksConstraintMultiplicities3(_,_,_,[]) :- !.

linksConstraintMultiplicities3(OClassA,N,N,LA) :-
  !,
  ( foreach(Obj, OClassA), 
    param(LA,N) 
    do
        getOid(Obj, Oid),
	%printf("ic_global:occurrences(%w,%w,%w)",[Oid,LA,N]),nl,
      ic_global:occurrences(Oid,LA,N)
  ).

linksConstraintMultiplicities3(OClassA,Min,Max,LA) :-
  length(LA,N),
  (Max == "*" -> Max1 = N; Max1 = Max), 
  ( foreach(Obj, OClassA),fromto([],In,Out,Constraints),param(Min,Max1) 
    do
        getOid(Obj, Oid),
	Out=[gcc(Min,Max1,Oid)|In]
  ),
  reverse(Constraints,ConstraintsR),
  % printf("ic_global_gac:gcc(%w)", [ConstraintsR]),nl,
  ic_global_gac:gcc(ConstraintsR,LA).

%% linksConstraintMultiplicities3(OClassA,1,_,LA) :-
%%  !,
%%  ( foreach(Obj, OClassA), 
%%     param(LA) 
%%     do
%%         getOid(Obj, Oid),
%% 	(foreach(LOid,LA),fromto(0,In,Out,1),param(Oid) do #=(Oid,LOid,Eq),ic:or(In,Eq,Out))
%%   ).
%% linksConstraintMultiplicities3(_,_,_,_).


linksConstraintMultiplicities4(OClassA,_,_,LA,N,N,_,_) :-
	!,
	linksConstraintMultiplicitiesAux(OClassA,N,LA).

linksConstraintMultiplicities4(_,N,N,_,OClassB,_,_,LB) :-
	!,
	linksConstraintMultiplicities4Aux(OClassB,N,LB).

linksConstraintMultiplicities4(_,_,_,LA,_,_,_,_) :-
        % printf("ic_global:ordered(=<, %w)",[LA]),nl,
	ic_global:ordered(=<, LA).

linksConstraintMultiplicities4Aux(OClassA,N,LA) :-
  !,
  length(OClassA,LOClassA),
  length(LA,LLA),
  LLA is LOClassA * N,
  N1 is LOClassA - 1,
  ( count(I,0,N1), foreach(Obj,OClassA),param(LA,N) do 
      getOid(Obj,Oid),
      N2 is N - 1,
      ( count(J,0,N2),param(I,Oid,LA,N) do
          K is I * N + J,
	  listut:nth0(K,LA,Oid) ) ).
	  
% noSharing(ListOfLinkLists) :-
% A class represented by its oid should not be linked  
% to more than one containment link

noSharing(ListOfLinkLists) :-
    %printf("ListOfLinkLists %w", [ListOfLinkLists]),nl,
	flatten(ListOfLinkLists,ListOfLinks),
	(foreach(Link,ListOfLinks),foreach(CompOid,CompOids) do arg(2,Link,CompOid)),
	%printf("CompOids %w", [CompOids]),nl,
	ic_global:alldifferent(CompOids).
	
	
differentLinks(LinkList):- 
  differentList(differenceForLinks, LinkList).

% differenceForLinks(LinkA, LinkB) :-
%    Constrain two links to be different: at least one participant must
%    be different

differenceForLinks(LinkA, LinkB) :-
  ( foreacharg(ParticipantA, LinkA),
    foreacharg(ParticipantB, LinkB),
    fromto(0, In, Out, Result)
    do
       % Aux = (ParticipantA <> ParticipantB)
       #\=(ParticipantA, ParticipantB, Aux),
       ic:or(In, Aux, Out)
  ),
  % Result is PartA1 <> PartB1 or PartA2 <> PartB2 or ...
  Result #= 1.

% orderedLinks(LinkList) :-
%    All links of an association must be defined in some order
%    This restriction is imposed for efficiency reasons to avoid
%    exploring all possible permutations of links within an association

orderedLinks(Instances,Assoc) :- 
  index(Assoc, IndexAssoc),
  nth1(IndexAssoc, Instances, LAssoc),

  %% % Get the minimum and maximum cardinalities for both ends
  %% % of the association
  roleIndex(Assoc,RoleA,1),
  roleIndex(Assoc,RoleB,2),
  roleMin(Assoc, RoleA, MinA),
  roleMax(Assoc, RoleA, MaxA),
  roleMin(Assoc, RoleB, MinB),
  roleMax(Assoc, RoleB, MaxB),
  orderedLinks1(LAssoc, MinA,MaxA,MinB,MaxB).


orderedLinks1(LAssoc,N,N,_,_) :-
	!,
	orderedList(orderForLinks2, LAssoc).

orderedLinks1(LAssoc,_,_,_,_) :-
	!,
	orderedList(orderForLinks1, LAssoc).

% orderForLinks1(LinkA, LinkB) :-
%    Constrain LinkA to be strictly less than LinkB. Let p(i,L) be the i-th 
%    participant of L, then LinkA is less than LinkB if:
%      ( p(1,LA) < p(1,LB) ) OR
%      ( p(1,LA) = p(1,LB) AND p(2,LA) < p(2,LB) ) OR
%      ( p(1,LA) = p(1,LB) AND p(2,LA) = p(2,LB) AND p(3,LA) < p(3,LB) ) OR
%      ...

orderForLinks1(LinkA, LinkB) :-
  ( foreacharg(ParticipantA, LinkA), 
    foreacharg(ParticipantB, LinkB),
    fromto(0, InOrder, OutOrder, Result),
    fromto(1, InEquals, OutEquals, _)
    do
      #<(ParticipantA, ParticipantB, Less),
      #=(ParticipantA, ParticipantB, Equals),              

      % InEquals = participants 1 to I-1 are equal
      % OutEquals should extend it up to the I-th participant    
      % OutEquals = InEquals /\ Equals
      ic:and(InEquals, Equals, OutEquals),      

      % InOrder = participants 1 to I-1 are ordered
      % OutOrder should extend it up to the I-th participant
      % OutOrder = InOrder \/ (InEquals /\ Less) 
      ic:and(InEquals, Less, Aux),

      ic:or(InOrder, Aux, OutOrder) 
  ),
  Result #=1.

% orderForLinks2(LinkA, LinkB) --- reverse components
orderForLinks2(LinkA, LinkB) :-
  (foreacharg(ParticipantA,LinkA),fromto([],X,Y,LinkAReversed) do Y = [ParticipantA|X]),
  (foreacharg(ParticipantB,LinkB),fromto([],X,Y,LinkBReversed) do Y = [ParticipantB|X]),
  ( foreach(ParticipantA, LinkAReversed), 
    foreach(ParticipantB, LinkBReversed),
    fromto(0, InOrder, OutOrder, Result),
    fromto(1, InEquals, OutEquals, _)
    do
      #<(ParticipantA, ParticipantB, Less),
      #=(ParticipantA, ParticipantB, Equals),              

      % InEquals = participants 1 to I-1 are equal
      % OutEquals should extend it up to the I-th participant    
      % OutEquals = InEquals /\ Equals
      ic:and(InEquals, Equals, OutEquals),      

      % InOrder = participants 1 to I-1 are ordered
      % OutOrder should extend it up to the I-th participant
      % OutOrder = InOrder \/ (InEquals /\ Less) 
      ic:and(InEquals, Less, Aux),

      ic:or(InOrder, Aux, OutOrder) 
  ),
  Result #=1.


%------------------------------------------------------------------------------
%
% Inheritance - disjoint, complete, ...
% 
%------------------------------------------------------------------------------


% constraintsSubtypesCard(Super, Subs) :-
%    Cardinality of subtypes cannot be greater than the cardinality of the supertype

constraintsSubtypesCard(Super, Subs):-
  ( foreach(Si, Subs), param(Super) do Super#>=Si ). 

% constraintsDisjointSubtypesCard(Super, Subs) :-
%    Cardinality of subtypes cannot be greater than the cardinality of the supertype
%    If the inheritance is "disjoint", the sum of instances of all subclasses cannot
%    be more than the cardinality of the supertype

constraintsDisjointSubtypesCard(Super, Subs):-
  ( Super #>= sum(Subs) ). 

% constraintsAbstract DisjointSubtypesCard(Super, Subs) :-
%    Cardinality of subtypes cannot be greater than the cardinality of the supertype
%    If the inheritance is "disjoint", the sum of instances of all subclasses cannot
%    be more than the cardinality of the supertype. Also, if the class is abstract
%    the superclass cannot have more objects.

constraintsAbstractDisjointSubtypesCard(Super, Subs):-
  ( Super #= sum(Subs) ). 

% existsOidIn(OSub, OSuper):-
%    Oids of subtype instances must exist in the list of oids for the supertype

existsOidIn(OSub, OSuper) :-
   getOidList(OSub, OidListSub),
   getOidList(OSuper, OidListSuper),
   ( foreach(Oid, OidListSub),
     param(OidListSuper)
     do
        existsOidInList(Oid, OidListSuper)
   ).

% existsOidInOneOf(OAbstract, ListOfObjectLists) :-
% if the super type is Abstract then it's Oids must exist in the one of the subtypes

existsOidInOneOf(OAbstract, ListOfObjectLists) :-
   flatten(ListOfObjectLists,All),
   getOidList(OAbstract, OidListAbstract),
   getOidList(All, OidListAll),
   ( foreach(Oid, OidListAbstract),
     param(OidListAll)
     do
        existsOidInList(Oid, OidListAll)
   ).

% disjointOids( TypeList ):-
%    All Oids of the classes within TypeList must be distinct 

disjointOids( TypeList ) :-
        ( foreach( ObjList, TypeList),
          fromto([], OidIn, OidOut, OidList)
          do (
            getOidList( ObjList, OidList ),
            append( OidList, OidIn, OidOut)          
          ) ),
        flatten( OidList, AllOids),
        ic_global:alldifferent( AllOids ).       

%------------------------------------------------------------------------------
%
% Auxiliary methods
% 
%------------------------------------------------------------------------------

% nth1( N, List, Value) :-
%    Get the value in the n-th position of the list
%    Consider that the first element of the list is in position 1

nth1( _, [], _ ) :- 
   writeln("Internal error"),
   writeln(" Term nth1/3 invoked with empty list"), 
   abort. 

nth1( N, [H|T], X) :-
   ( N > 1 ->
     Aux is N-1,
     nth1( Aux, T, X);
     X = H ).

% orderedList(OrderCriterion, List) :-
%    Check if a List is ordered according to some ordering criterion (e.g. #<),
%    such that ord(X,Y) succeds if X <= Y

orderedList(OrderCriterion, List) :-
   ( List = [] -> true;  
     ( fromto(List, [This,Next|Rest], [Next|Rest], [_]),
       param(OrderCriterion)
       do
          apply(OrderCriterion, [This, Next])
      )
   ).

% differentList(DifferCriterion, List) :-
%    Check if all elements of a List are different, according to some
%    "difference" criterion (e.g. #\=), such that dif(X,Y) succeeds if X<>Y

differentList(DifferCriterion, List) :-
  ( fromto(List, [This|Rest], Rest, []),
    param(DifferCriterion)
    do
      ( foreach(Other,Rest), 
        param(This,DifferCriterion)
        do
          apply(DifferCriterion, [This, Other])
      )
  ).

% getPartList( LinkList, RoleIndex, OidList ) :-
%    Computes the list of participants in a given role of an association
%    The list has as many elements as links in the association

getPartList(LinkList, RoleIndex, OidList) :-
   ( foreach(Link, LinkList),
     foreach(Oid,  OidList),
     param(RoleIndex) 
     do
        arg(RoleIndex, Link, Oid) 
   ). 

ocl_allInstances2(Instances, TypeName, Result) :-
	index(TypeName, TypeIndex),
	nth1(TypeIndex, Instances, ObjectList),
	getOidList(ObjectList, Result).

parameterOfBasicType(Par, Domain) :-
	% Par must have a value within Domain
	% Domain is provided by the designer
	Par::Domain.

parameterOfObjectType(Par, Snapshot, ParType) :-	
	% Par must be an object of ParType within Snapshot
	getObjects(Snapshot, ParType, Objects),
	getOidList(Objects, OidList),
	existsOidInList(Par, OidList).

getObjects(Snapshot, Type, List) :-
	index(Type, Index),
	nth1(Index, Snapshot, List).

ocl_isNew( PostState, TypeName, Oid, Result ) :-
   % Recover the instances in the previous state
   getval( initialstate, PreState ),
   index(TypeName, Index),
   % Get the set of objects before and after
   nth1(Index, PreState, InstancesPre),
   nth1(Index, PostState, InstancesPost),
   % Get the list of oids before and after
   getOidList( InstancesPre, OidListPre ),
   getOidList( InstancesPost, OidListPost ),
   % Result is true if Oid appears in OidListPost but not in OidListPre
   aux_OidInList( Oid, OidListPre, InPre ),
   aux_OidInList( Oid, OidListPost, InPost ),
   ic:neg(InPre, NotInPre),
   ic:and(InPost, NotInPre, Result).   


% aux_OidInList( Oid, OidList, Result ) :-
%    Result is true if Oid appears in OidList and false otherwise
delay aux_OidInList(X, _, _) if var(X).
delay aux_OidInList(_, X, _) if nonground(X).
aux_OidInList(Oid, OidList, Result) :-
   ( foreach(Other, OidList),
     fromto(0, In, Out, Result),
     param(Oid)
     do 
       #=(Oid, Other, Aux),
       ic:or(In, Aux, Out)
   ).


% ocl_attributeCall2( Instances, TypeName, AttribName, Oid, Result ) :-
%    Result is the value of the attribute AttrName of the object with Oid within
%    TypeName in a given instance

delay ocl_attributeCall2( _, _, _, X, _) if var(X). 
ocl_attributeCall2( Instances, TypeName, AttribName, Oid, Result ) :-
  % Check if the attribute was declared in this type or in a superclass
  ( % Check whether there is an attribute AttribName in this class
    attIndex( TypeName, AttribName, IndexAttr) 
    ->
    % Get the objects of class TypeName 
    index(TypeName, IndexType),
    nth1(IndexType, Instances, ObjectList),
    % Find the object with the current oid
    findObjectByOid(ObjectList, Oid, Object),
    arg(IndexAttr, Object, Result)    
    ; 
    % Check if the attribute is declared in a supertype
    isSubTypeOf( TypeName, SuperClass ),
    ocl_attributeCall2( Instances, SuperClass, AttribName, Oid, Result )
  ).  


delay ocl_navigation2(_,_,_,_,X,_) if var(X).
ocl_navigation2(Instances, Association, SrcRole, DstRole, Objects, Result) :-
   % Get the list of oids of the source objects
   ( is_list(Objects) -> 
     % Objects is a list of several objects
     OidList = Objects;
     % Objects is a single object outside a list
     OidList = [objects] 
   ),
   % Get the list of links of the association
   index(Association, AssocIndex),
   nth1(AssocIndex, Instances, LinkList),

   % Get the role indices
   roleIndex(Association, SrcRole, SrcIndex),
   roleIndex(Association, DstRole, DstIndex),

   % Get the set of links of the association where SrcIndex equals
   % an object within OidList. Returns a list of oids in the 
   % target relation
   aux_navigate(LinkList, SrcIndex, DstIndex, OidList, ResultObjList),	
   aux_extract_from_list2(ResultObjList, Result).

delay aux_extract_from_list2(X,_) if nonground(X).
aux_extract_from_list2([],_).
aux_extract_from_list2([X|[]], X).
aux_extract_from_list2([X|[H|T]], [X|[H|T]]).
