% 2007/05/17
% - TODO:
%   + Inheritance is not fully supported
%   + N-ary associations are not fully supported
%   + Boolean values are written numerically
%   + Attributes of type string are also not supported
% 2009/06/12
% - Added inheritance support
 
%
% ECLiPSe libraries
%

tool_name("EMFtoCSP").
tool_version("1.0").

% gviz_draw_object_diagram(+FileName, Instances):-
%   Write an object diagram to FileName that depicts the objects
%   and links. The file is written in GraphViz format.
%   TODO: inheritance hierarchies are still not supported:
%   - Objects are printed both in the subclass and the superclass
%   - The naming criteria for classes within the Graphviz file
%     is not very consistent and may fail in the presence of
%     an inheritance hierarchy (e.g. a link to a superclass may fail
%     to identify the correct node it is referring to).
%  TODO: n-ary links are not currently supported.

gviz_draw_object_diagram(FileName, Instances) :-
   % Create the Graphviz file
   gviz_create_file(FileName, Stream),
   % Write the header
   gviz_start_file(Stream),
   % Write all objects and links in the file
   (foreach(TypeInstances, Instances),
    count(Idx, 1, _),
    param(Stream, Instances)
    do
      % Get the name of this type
      index(TypeName, Idx),
      % Test whether this is a class or an association
      % Associations will have a "isUnique" flag
      %   
      ( is_predicate(assocIsUnique/2), assocIsUnique(TypeName,_) ->
        % Association
        gviz_write_assoc(Stream, TypeInstances, TypeName);
        % Class
        ( is_predicate(isSubTypeOf/2), isSubTypeOf( TypeName, _) -> 
           % The class has a parent
           % No need to print the file, it will be printed by the base type
           true
           ;
           gviz_write_class(Stream, Instances, TypeInstances, TypeName)
         )
      )
   ),
   % Write the conclusion and close the file
   gviz_end_file(Stream).

% TODO - var 1 is the parameter list
gviz_draw_object_diagram(FileName, Instances, _) :-
   % Create the Graphviz file
   gviz_create_file(FileName, Stream),
   % Write the header
   gviz_start_file(Stream),
   % Write all objects and links in the file
   (foreach(TypeInstances, Instances),
    count(Idx, 1, _),
    param(Stream)
    do
      % Get the name of this type
      index(TypeName, Idx),
      % Test whether this is a class or an association
      % Associations will have a "isUnique" flag
      %   
      ( is_predicate(assocIsUnique/2), assocIsUnique(TypeName,_) ->
        % Association
        gviz_write_assoc(Stream, TypeInstances, TypeName);
        % Class
        gviz_write_class(Stream, TypeInstances, TypeName)
      )
   ),
   % Write the conclusion and close the file
   gviz_end_file(Stream).

% TODO - var 1 is the parameter list
%        var 2 is the final instance
gviz_draw_object_diagram(FileName, Instances, _, _) :-
   % Create the Graphviz file
   gviz_create_file(FileName, Stream),
   % Write the header
   gviz_start_file(Stream),
   % Write all objects and links in the file
   (foreach(TypeInstances, Instances),
    count(Idx, 1, _),
    param(Stream)
    do
      % Get the name of this type
      index(TypeName, Idx),
      % Test whether this is a class or an association
      % Associations will have a "isUnique" flag
      %   
      ( is_predicate(assocIsUnique/2), assocIsUnique(TypeName,_) ->
        % Association
        gviz_write_assoc(Stream, TypeInstances, TypeName);
        % Class
        gviz_write_class(Stream, TypeInstances, TypeName)
      )
   ),
   % Write the conclusion and close the file
   gviz_end_file(Stream).

% TODO - var 1 is the parameter list
%        var 2 is the final instance
%        var 3 is the result
gviz_draw_object_diagram(FileName, Instances, _, _, _) :-
   % Create the Graphviz file
   gviz_create_file(FileName, Stream),
   % Write the header
   gviz_start_file(Stream),
   % Write all objects and links in the file
   (foreach(TypeInstances, Instances),
    count(Idx, 1, _),
    param(Stream)
    do
      % Get the name of this type
      index(TypeName, Idx),
      % Test whether this is a class or an association
      % Links will have a "isUnique" flag
      ( is_predicate(assocIsUnique/2), assocIsUnique(TypeName,_) ->
        % Association
        gviz_write_assoc(Stream, TypeInstances, TypeName);
        % Class
        gviz_write_class(Stream, TypeInstances, TypeName)
      )
   ),
   % Write the conclusion and close the file
   gviz_end_file(Stream).


% gviz_create_file(+FileName,-Stream) :-
%   Create a graphviz file with name Filename

gviz_create_file(FileName, Stream) :-
  open(FileName, write, Stream, [end_of_line(crlf)]).

% gviz_start(+Stream) :-
%   Write the header of the Graphviz file.   

gviz_start_file(Stream) :-
   writeln(Stream, "graph {"),
   % Attributes of the entire graph 
   writeln(Stream, ""),
   writeln(Stream, "  // Global attributes"),
   writeln(Stream, "  graph [fontname=Verdana,"),
   writeln(Stream, "         fontsize=12,"),
   writeln(Stream, "         labeljust=\"c\","),
   writeln(Stream, "         labelloc=\"b\","),
   writeln(Stream, "         fontcolor=blue,"),
   writeln(Stream, "         center=true];"),
   % Attributes for nodes  
   writeln(Stream, "  node  [fontname=Verdana,"),
   writeln(Stream, "         style=filled,"),
   writeln(Stream, "         fillcolor=lightgray];"),
   % Attributes for edges
   writeln(Stream, "  edge  [fontname=Verdana];"),
   writeln(Stream, ""),  
   % Label of the graph
   date(SystemDate),
   % Remove the last character of the date (\n),
   string_length(SystemDate, DateLength),
   NewLength is DateLength - 1,
   substring(SystemDate, 1, NewLength, Date), 
   tool_name(Name),
   tool_version(Version),
   writeln(Stream, "// Label of the graph"),
   printf(Stream, "graph[label=\"-- Object diagram--\\nGenerated by %sv%s on %s\"];",
          [Name, Version, Date]),
   writeln(Stream, "").


% gviz_write_assoc(+Stream, +Instances, +TypeName):-
%   Write all links (Instances) of a given association of name TypeName.

gviz_write_assoc(Stream, Instances, TypeName) :-
   (foreach(Link, Instances),
    param(Stream, TypeName)
    do
      gviz_write_link(Stream, Link, TypeName)
   ).    

% gviz_write_class(+Stream, +Instances, +TypeInstances, +TypeName):-
%   Write all objects (TypeInstances) of a given class of name TypeName.

gviz_write_class(Stream, Instances, TypeInstances, TypeName) :-
   (foreach(Object, TypeInstances),
    param(Stream, TypeName, Instances)
    do
      gviz_write_object(Stream, Instances, Object, TypeName)
   ).    



% gviz_write_attributes(+Stream, +Object, +TypeName):-
%   Write all attributes of a given object within class TypeName.

gviz_write_attributes(Stream, Object, TypeName) :-
  (foreacharg(AttribValue, Object, Idx),
   param(Stream, TypeName)
   do
     (Idx = 1 -> 
      % Ignore the oid field
      true;
      attIndex(TypeName, AttribName, Idx),
      attType(TypeName, AttribName, AttType),
      % Write another attribute
      gviz_att_value_to_string(AttribValue,AttType,VS),
      printf(Stream, "- %s = '%w'\\l", [AttribName, VS])
     )
   ).

gviz_att_value_to_string(Value,"EString", VS) :- !, gviz_att_value_to_string(Value,"String",VS).

gviz_att_value_to_string(Value,"String", VS) :-
        is_list(Value),
	!,
	(foreach(V,Value),fromto("",V1,V2,VS) do 
            ( V :: [38,39,60,62,91,92,93,123,125] -> sprintf(X,"\\%c",[V]); sprintf(X,"%c",V)),
            append_strings(V1,X,V2)
        ).

gviz_att_value_to_string(Value,_ , VS) :-
	sprintf(VS,"%w",Value).

% gviz_type_list(+Instances, +Object, +TypeName, -TypeList, -ObjList) :-
%   Given an Object of a given TypeName, compute the list of subtypes of TypeName
%   where the Object belongs. The most concrete type should be the first type
%   of the list.

gviz_findObjectByOid([], _, _) :- fail.
gviz_findObjectByOid([H|T], Oid, Obj) :-
   getOid(H,O),
   ( O = Oid -> 
     Obj = H ;
     gviz_findObjectByOid(T, Oid, Obj) ).

gviz_type_list(Instances, Object, TypeName, TypeList, ObjList) :- 
   % Check if the type has a subtype
   is_predicate(isSubTypeOf/2),
   isSubTypeOf(SubType, TypeName),
   % Check if the oid of the object is a valid oid of the subclass
   % Get the oid of the object
   getOid(Object, Oid),
   % Get the list of oids of the objects of the subclass
   index(SubType, Index),
   nth1(Index, Instances, ObjectList),
   % Find the object with that oid within 
   gviz_findObjectByOid( ObjectList, Oid, Obj),     
   % Recursive call
   gviz_type_list(Instances, Object, SubType, TypeAuxList, ObjAuxList),
   append(TypeAuxList,[TypeName], TypeList),
   append(ObjAuxList,[Obj], ObjList).

gviz_type_list(_, Object, TypeName, TypeList, ObjList) :- 
   TypeList = [ TypeName ],
   ObjList = [ Object ],
   !.
   
% gviz_write_object(+Stream, +Instances, +Object, +TypeName):-
%   Write all information about an instance of a class to the Graphviz file. 
%   All fields but the oid are written to the class, while the oid is used
%   to define a name for this object.

gviz_write_object(Stream, Instances, Object, TypeName) :-
  % The identifier used within the GraphViz file will be   
  % type name in lower case + oid of the object:
  getOid(Object, Oid),
  % Get the list of types of the object (the object may have attributes
  % in a class, one of its subclasses, ...)
  gviz_type_list(Instances, Object, TypeName, TypeList, ObjList),
  % Get the most concretetype
  nth1(1, TypeList, MostConcreteType), 
  printf(Stream, "%s%d [shape=record,label=\"{%s%d: %s |", 
         [TypeName, Oid, MostConcreteType, Oid, MostConcreteType]),
  % Write the list of attributes of the object
  (foreach(Type, TypeList),
   foreach(Obj, ObjList),
   param(Stream)
   do
     gviz_write_attributes(Stream, Obj, Type)
  ),
  writeln(Stream,"}\"];").     

% gviz_write_link(+Stream, +Link, +TypeName) :-
%   Write a Link with type TypeName.

gviz_write_link(Stream, Link, TypeName) :-
   % Get the arity of the link
   functor(Link, _, Arity),
   % Binary and n-ary links will be drawn in a different way.
   (Arity = 2 ->
    gviz_write_binary_link(Stream, Link, TypeName);
    gviz_write_nary_link(Stream, Link, TypeName)
   ).

% gviz_write_binary_link(+Stream, +Link, +TypeName) :-
%   Write a binary Link between two objects.

gviz_write_binary_link(Stream, Link, TypeName) :-
   % The Graphviz syntax is
   % node1 -- node2 [label="Typename"];
   % Get Oids of participating objects
   arg(1, Link, Oid1),
   arg(2, Link, Oid2),
   % Get role names (needed to access role types)
   roleIndex(TypeName, RoleName1, 1),
   roleIndex(TypeName, RoleName2, 2),
   % Get role types
   roleType(TypeName, RoleName1, RoleType1),
   roleType(TypeName, RoleName2, RoleType2),
   % Locate the base types for each role type
   % The identifier of the objects within the GraphViz file is
   % BaseTypeName + ObjectIdentifier
   aux_baseType(RoleType1, BaseRoleType1),
   aux_baseType(RoleType2, BaseRoleType2),
   printf(Stream, "%s%d -- %s%d [label=\"%s\"];", 
         [BaseRoleType1, Oid1, BaseRoleType2, Oid2, TypeName]),
   writeln(Stream, "").     
  
% gviz_write_nary_link(+Stream, +Link, +TypeName) :-
%   Write a n-ary Link between two objects.
%   TODO: Missing!

gviz_write_nary_link(_, _, _) :-
   true.
 

% gviz_end_file(Stream) :-
%    Write the conclusion of the Graphviz file.

gviz_end_file(Stream) :-
   writeln(Stream, ""),
   writeln(Stream, "}"),
   close(Stream).
     
     
