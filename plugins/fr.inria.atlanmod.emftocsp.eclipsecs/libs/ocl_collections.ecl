%
% OCL Standard Library - Collection classes 
% 
% Implemented classes:
% - Collection  
% - Set
% - Bag
% - Sequence
% - OrderedSet

% 2007/03/29:
%
% Operations that do not adhere to the standard
% - flatten() and sum() do not work properly so far because they
%   require knowing the type of the elements of the collection
%
% Incorrect behavior in the standard
% - The post of OrderedSet.append(), OrderedSet.prepend() and 
%   OrderedSet.insertAt() is incorrect as it does not guarantee
%   the absence of duplicates after the insertion 
%
% Undefined behaviors in the standard
% - Order of the elements in the collection returned by asSequence()
% - Behavior of Sequence.inserAt() and OrderedSet.insertAt() if the index is 
%   outside bounds
% - Behavior of Sequence.indexOf() if the object does not appear in 
%   the sequence or it appears multiple times (the position being returned 
%   is not specified)
% - Behavior of first() and last() in empty Sequences and OrderedSets
%
% 2007/07/06:
% - Changed several operations to avoid a bug when navigation 
%   returned a single object (ocl_mustBeObject)
% 2007/07/06:
% - Changed several operations to speed up operations on empty
%   collections (include, exclude, includeAll, excludeAll, ...)

%
% ECLiPSe libraries
%
:- lib(ic).       % Interval constraints
:- lib(lists).    % List manipulation
:- lib(ordset).   % Ordered set manipulation

%------------------------------------------------------------------------------
%
% Collection - A generic collection of elements backed by a Prolog list
% 
%------------------------------------------------------------------------------

delay ocl_obj_asSet(X,Y) if var(X).
ocl_obj_asSet(ocl_undef,[]) :- !.
ocl_obj_asSet([X],[X]) :- !.
ocl_obj_asSet(X,[X]).



% ocl_col_size( +Col, -Size ) :-
%    Returns the number of elements in the collection

delay ocl_col_size(X, _) if var(X).
ocl_col_size(Col, Size) :- 
  length(Col, Size).

% ocl_col_includes( +Col, +Obj, -Result ) :-
%    Tests if object Obj is inside Col. In that case, 
%    Result = 1 if Obj is in Col, 0 otherwise.

delay ocl_col_includes(X, _, _) if var(X).
delay ocl_col_includes(_, Y, _) if var(Y).
ocl_col_includes(_, ocl_undef, 0) :- !.
ocl_col_includes(Col, Obj, Result) :- 
   Result::0..1,
   (foreach(X,Col),fromto(0,In,Out,Result),param(Obj) do
       ocl_obj_equals(Obj,X,B),
       Out #= B or In).
  
% ocl_col_excludes( +Col, +Obj, -Result ) :- 
%    Tests if object Obj is inside Col. In that case, 
%    Result = 1 if Obj is in Col, 1 otherwise.

delay ocl_col_excludes(Col, Obj, Result) if var(Col).
delay ocl_col_excludes(Col, Obj, Result) if var(Obj).
ocl_col_excludes(Col, Obj, Result) :-
   Result::0..1,
   ocl_mustBeObject(Obj, Object),
   ocl_col_includes(Col, Object, Aux),
   ic:neg(Aux, Result).

% ocl_col_count( +Col, +Obj, -Result ) :-
%    Result is the number of instances of Obj within Col

delay ocl_col_count(X, _, _) if nonground(X).
delay ocl_col_count(_, Y, _) if nonground(Y).
ocl_col_count( Col, Obj, Result ) :-
  Result::0..1,
  ocl_mustBeObject(Obj, Object),
  ( foreach(Elem, Col),
    fromto(0, In, Out, Result),
    param(Object) 
    do
      ( Elem = Object -> Out is In + 1; Out is In )
  ).

% ocl_col_includesAll( +Col1, +Col2, -Result ) :- 
%    Result is 1 if Col1 includes all elements of Col2
%    and 0 otherwise

delay ocl_col_includesAll(X, _, _) if nonground(X).
delay ocl_col_includesAll(_, Y, _) if nonground(Y).
ocl_col_includesAll(Col1, Col2, Result) :-
   ( Col2 = [] -> 
     Result = 1; 
     sort(Col1, OrderedCol1),
     sort(Col2, OrderedCol2),
     ( subset(OrderedCol2, OrderedCol1) ->
       Result = 1;
       Result = 0 
     )
   ).   

% ocl_col_excludesAll( +Col1, +Col2, -Result) :-
%    Result is 1 if Col1 does not include any element of Col2
%    and 0 otherwise

delay ocl_col_excludesAll(X, _, _) if nonground(X).
delay ocl_col_excludesAll(_, Y, _) if nonground(Y).
ocl_col_excludesAll(Col1, Col2, Result) :-
   ( Col2 = [] ->
     Result = 1;
     ( Col1 = [] ->
       Result = 1;
       list_to_ord_set(Col1, OrderedCol1),
       list_to_ord_set(Col2, OrderedCol2),
       ( ord_intersect(OrderedCol1, OrderedCol2) ->
         Result = 0;
         Result = 1 
       )
     ) 
   ). 

% ocl_col_isEmpty( +Col, -Result ) :-
%    Result is 1 if the collection is empty, 0 otherwise

delay ocl_col_isEmpty(X, _) if var(X).
ocl_col_isEmpty([], 1) :- !.
ocl_col_isEmpty([_|_], 0) :- !.

% ocl_col_notEmpty( +Col, -Result ) :-
%    Result is 1 if the collection is not empty, 0 otherwise

delay ocl_col_notEmpty(X, _) if var(X).
ocl_col_notEmpty([], 0) :- !.
ocl_col_notEmpty([_|_], 1) :- !.

% ocl_col_sum( ?Col, ?Result ) :-
%    Result is the sum of all elements within the collection
%    Warning: currently this method only works properly for
%    collections of integers

delay ocl_col_sum(X, _) if var(X).
ocl_col_sum(Col, Result) :- Result #= sum(Col).
                                          
% ocl_col_product( +Col1, +Col2, +Result ) :- 
%    Result is a set with the cartesian product of the elements
%    of two collections. The result should be a set, so duplicates 
%    should be removed

delay ocl_col_product(X, _, _) if nonground(X).
delay ocl_col_product(X, _, _) if nonground(X).
ocl_col_product([], _, []) :- !.
ocl_col_product(_, [], []) :- !.
ocl_col_product([First1|Rest1], [First2|Rest2], Result) :-
                cartesian_product(First1, [First2|Rest2], Aux1),
                ocl_col_product(Rest1, [First2|Rest2], Aux2),
                append(Aux1, Aux2, Aux3),
                sort(Aux3, Result).

% cartesian_product(X,Y,Z) 
%    Combine X with each element of list Y
%    The result will be stored in the list Z

cartesian_product(Obj, Col, Result) :-
   ( foreach(Elem, Col),
     foreach([Obj, Elem], Result),
     param(Obj)
     do 
        true
   ).
                                  
%------------------------------------------------------------------------------
%
% Set - A set (unordered collection of elements without duplicates). 
%
%------------------------------------------------------------------------------

% Operations inherited from collection
ocl_set_size(Set, Size)               :- ocl_col_size(Set, Size).
ocl_set_includes(Set, Obj, Result)    :- ocl_col_includes(Set, Obj, Result).
ocl_set_excludes(Set, Obj, Result)    :- ocl_col_excludes(Set, Obj, Result).
ocl_set_count(Set, Obj, N)            :- ocl_col_count(Set, Obj, N).
ocl_set_includesAll(Set, Col, Result) :- ocl_col_includesAll(Set, Col, Result).
ocl_set_excludesAll(Set, Col, Result) :- ocl_col_excludesAll(Set, Col, Result).
ocl_set_isEmpty(Set, Result)          :- ocl_col_isEmpty(Set, Result).
ocl_set_notEmpty(Set, Result)         :- ocl_col_notEmpty(Set, Result).
ocl_set_sum(Set, N)                   :- ocl_col_sum(Set, N).
ocl_set_product(Set, Col, Product)    :- ocl_col_product(Set, Col, Product). 

% ocl_set_including( +Set, +Obj, -Result ) :-
%    Add Obj to Set if it was not already there, Result stores the new set

delay ocl_set_including(X, _, _) if nonground(X).
delay ocl_set_including(_, Y, _) if nonground(Y).
ocl_set_including(Set, Obj, Result) :-
   ocl_mustBeObject(Obj, Object),
   list_to_ord_set(Set, Aux),
   ord_insert(Aux, Object, Result).

% ocl_set_union( +Set1, +Set2, -Result) :- 
%    Result is the union of Set1 and Set2    

delay ocl_set_unionSet(X, _, _) if nonground(X).
delay ocl_set_unionSet(_, Y, _) if nonground(Y).
ocl_set_unionSet(Set1, Set2, Result) :- 
   list_to_ord_set(Set1, Aux1),
   list_to_ord_set(Set2, Aux2),
   ord_union(Aux1, Aux2, Result).

% ocl_set_union( +Set, +Bag, -Result ) :-
%    Result is the union of Set and Bag
%    A Bag can hold multiple copies of the same element

delay ocl_set_unionBag(X, _, _) if nonground(X).
delay ocl_set_unionBag(_, Y, _) if nonground(Y).
ocl_set_unionBag(Set, Bag, Result) :-
   list_to_ord_set(Set, Aux1),
   list_to_ord_set(Bag, Aux2),    
   ord_union(Aux1, Aux2, Result).  

% ocl_set_equals( +Set1, +Set2, -Result ) :- 
%    Result is true if Set1 = Set2

delay ocl_set_equals(X, _, _) if nonground(X).
delay ocl_set_equals(_, Y, _) if nonground(Y).
ocl_set_equals(Set1, Set2, Result) :- 
   list_to_ord_set(Set1, Aux1),
   list_to_ord_set(Set2, Aux2),
   ( Aux1 = Aux2 -> Result = 1; Result = 0 ).

% ocl_set_intersection( +Set1, +Set2, -Result ) :- 
%    Result is the intersection of Set1 and Set2

delay ocl_set_intersectionSet(X, _, _) if nonground(X).
delay ocl_set_intersectionSet(_, Y, _) if nonground(Y).
ocl_set_intersectionSet(Set1, Set2, Result) :- 
   list_to_ord_set(Set1, Aux1),
   list_to_ord_set(Set2, Aux2),
   ord_intersection(Aux1, Aux2, Result).

% ocl_set_intersection( +Set1, +Bag, -Result ) :-
%    Result is the intersection of Set and Bag
%    A Bag can hold multiple copies of the same element

delay ocl_set_intersectionBag(X, _, _) if nonground(X).
delay ocl_set_intersectionBag(_, Y, _) if nonground(Y).
ocl_set_intersectionBag(Set, Bag, Result) :- 
   list_to_ord_set(Set, Aux1),
   list_to_ord_set(Bag, Aux2),
   ord_intersection(Aux1, Aux2, Result).  

% ocl_set_difference( +Set1, +Set2, -Result ) :- 
%    Result is Set1 - Set2

delay ocl_set_difference(X, _, _) if nonground(X).
delay ocl_set_difference(_, Y, _) if nonground(Y).
ocl_set_difference(Set1, Set2, Result):- 
   list_to_ord_set(Set1, Aux1),
   list_to_ord_set(Set2, Aux2),
   ord_subtract(Aux1, Aux2, Result).
    
% ocl_set_excluding( +Set, +Obj, -Result ) :-
%    Result = Set - Obj

delay ocl_set_excluding(X, _, _) if nonground(X).
delay ocl_set_excluding(_, Y, _) if nonground(Y).
ocl_set_excluding(Set, Obj, Result ):- 
   ocl_mustBeObject(Obj, Object),
   list_to_ord_set(Set, Aux),
   ord_del_element(Aux, Object, Result).

% ocl_set_symmetricDifference( +Set1, +Set2, -Result ) :- 
%    Result holds the elements in Set1 and Set2 but not in both

delay ocl_set_symmetricDifference(X, _, _) if nonground(X).
delay ocl_set_symmetricDifference(_, Y, _) if nonground(Y).
ocl_set_symmetricDifference(Set1, Set2, Result) :-
   list_to_ord_set(Set1, Aux1),
   list_to_ord_set(Set2, Aux2),
   ord_symdiff(Aux1, Aux2, Result). 

% ocl_set_flatten( +Set, -Result ) :- 
%    Flattens the elements in SET if they are collections
%
% WARNING: this operation always flattens the elements in the list. If the list
% holds other types of data that are stored as Prolog lists, the results of 
% these operations are impredictable.

delay ocl_set_flatten(X, _) if var(X).
ocl_set_flatten(Set, Result) :- 
   flatten(Set, Result).

% ocl_set_asSet( +Set, -Result ) :- 
%    Returns the set itself

delay ocl_set_asSet(X, _) if var(X).
ocl_set_asSet(Set, Result) :- 
   ( is_list(Set) -> Result = Set; Result = [Set]).

% ocl_set_asOrderedSet( +Set, -Result ) :- 
%    Return the set, ordering all elements.

delay ocl_set_asOrderedSet(X, _) if nonground(X).
ocl_set_asOrderedSet(Set, Result) :- 
   ( is_list(Set) -> sort(Set, Result); Result = [Set] ).

% ocl_set_asSequence( +Set, -Result ) :- 
%    Return a Sequence view of the set
%    The order of elements is not specified

delay ocl_set_asSequence(X, _) if var(X).
ocl_set_asSequence(Set, Result) :- 
   ( is_list(Set) -> Result = Set; Result = [Set]).

% ocl_set_asBag( +Set, -Result ) :- 
%     Return a Bag view of the set

delay ocl_set_asBag(X, _) if var(X).
ocl_set_asBag(Set, Result) :- 
   ( is_list(Set) -> Result = Set; Result = [Set]).

%------------------------------------------------------------------------------
%
% Bag - A multiset (an unordered collection with potential duplicates).
%
%------------------------------------------------------------------------------

% Operations inherited from collection
ocl_bag_size(Bag, Size)               :- ocl_col_size(Bag, Size).
ocl_bag_includes(Bag, Obj, Result)    :- ocl_col_includes(Bag, Obj, Result).
ocl_bag_excludes(Bag, Obj, Result)    :- ocl_col_excludes(Bag, Obj, Result).
ocl_bag_count(Bag, Obj, N)            :- ocl_col_count(Bag, Obj, N).
ocl_bag_includesAll(Bag, Col, Result) :- ocl_col_includesAll(Bag, Col, Result).
ocl_bag_excludesAll(Bag, Col, Result) :- ocl_col_excludesAll(Bag, Col, Result).
ocl_bag_isEmpty(Bag, Result)          :- ocl_col_isEmpty(Bag, Result).
ocl_bag_notEmpty(Bag, Result)         :- ocl_col_notEmpty(Bag, Result).
ocl_bag_sum(Bag, N)                   :- ocl_col_sum(Bag, N).
ocl_bag_product(Bag, Col, Product)    :- ocl_col_product(Bag, Col, Product). 

% ocl_bag_equals( +Bag1, +Bag2, -Result ) :- 
%    Result is 1 if both bags hold the same set of elements and
%    each element appears the same number of times, 0 otherwise

delay ocl_bag_equals(X, _, _) if nonground(X).
delay ocl_bag_equals(_, Y, _) if nonground(Y).
ocl_bag_equals([], []) :- !.
ocl_bag_equals([First1|Rest1],[First2|Rest2]) :-
   msort([First1|Rest1], Aux1),
   msort([First2|Rest2], Aux2),
   Aux1 = Aux2.
       
% ocl_bag_union( +Bag1, +Bag2, -Result ) :-
%    Result = Bag1 union Bag2

delay ocl_bag_unionBag(X, _, _) if nonground(X).
delay ocl_bag_unionBag(_, Y, _) if nonground(Y).
ocl_bag_unionBag(Bag1, Bag2, Result) :- 
    append(Bag1, Bag2, Result).
       
% ocl_bag_union( +Bag, +Set, -Result ) :- 
%    Result = Bag union Set

delay ocl_bag_unionSet(X, _, _) if nonground(X).
delay ocl_bag_unionSet(_, Y, _) if nonground(Y).  
ocl_bag_unionSet(Bag, Set, Result) :- 
       append(Bag, Set, Result).
       
% ocl_bag_intersection( +Bag1, +Bag2, -Result ) :- 
%    Result is the intersection of Bag1 and Bag2 

delay ocl_bag_intersectionBag(X, _, _) if nonground(X).
delay ocl_bag_intersectionBag(_, Y, _) if nonground(Y).  
ocl_bag_intersectionBag(Bag1, Bag2, Result) :-
       msort(Bag1, SortedBag1),
       msort(Bag2, SortedBag2),
       intersect_with_duplicates(SortedBag1, SortedBag2, Result).              
              
% intersect_with_duplicates( +X, +Y, -Z ) :- 
%    X and Y are ordered lists, possibly with duplicates. Z should contain 
%    all elements that appear in both lists, as many times as the minimum
%    number of appearances of the element in X and Y

intersect_with_duplicates([], _, []) :- !.
intersect_with_duplicates(_, [], []) :- !.
intersect_with_duplicates([X|Rest1], [X|Rest2], Result) :-
       intersect_with_duplicates(Rest1, Rest2, Aux),
       Result is [X|Aux].
intersect_with_duplicates([First1|Rest1], [First2|Rest2], Result) :-
       First1 @< First2, !,
       intersect_with_duplicates(Rest1, [First2|Rest2], Result).
intersect_with_duplicates([First1|Rest1], [First2|Rest2], Result) :-
       First1 @> First2, !,
       intersect_with_duplicates([First1|Rest1], Rest2, Result).
       
% ocl_bag_intersection( +Bag, +Set, -Result ) :- 
%    Result = Bag intersection Set
%    No "delay" are required for this predicate, as the operation
%    is just a proxy towards ocl_set_interesectionBag/3.

ocl_bag_intersectionSet(Bag, Set, Result) :-
   ocl_set_intersectionBag(Bag, Set, Result).
      
% ocl_bag_including( +Bag, +Obj, -Result ) :- 
%    Result is the bag that holds all the elements of Bag plus Obj 
%    No "delay" are required for this predicate.

ocl_bag_including(Bag, Obj, [Object | Bag]) :- 
     ocl_mustBeObject(Obj, Object),
     !.

% ocl_bag_excluding( +Bag, +Obj, -Result ) :-
%    Result is a bag with all elements from Bag except Result

delay ocl_bag_excluding(X, _, _) if nonground(X).
delay ocl_bag_excluding(_, Y, _) if nonground(Y).
ocl_bag_excluding([], _, []) :- !.
ocl_bag_excluding([Object|Rest], Obj, Result) :-        
       ocl_mustBeObject(Obj, Object),
       ocl_bag_excluding(Rest, Object, Result).
ocl_bag_excluding([First|Rest], Obj, Result) :-        
       ocl_mustBeObject(Obj, Object),
       First \== Object, !,
       ocl_bag_excluding(Rest, Object, Aux),
       Result is [First|Aux].
       
% ocl_bag_flatten( +Bag, -Result ) :- 
%    Flattens the elements in BAG if they are collections
%
% WARNING: this operation always flattens the elements in the list. If the list
% holds other types of data that are stored as Prolog lists, the results of 
% these operations are impredictable.

delay ocl_bag_flatten(X,_) if var(X).
ocl_bag_flatten(Bag, Result) :- 
   flatten(Bag, Result). 
       
% ocl_bag_asSet( +Bag, -Result ) :- 
%    Returns a Set view of the Bag, removing duplicates

delay ocl_bag_asSet(X, _) if nonground(X).
ocl_bag_asSet(Bag, Result) :- 
   ( is_list(Bag) -> sort(Bag, Result); Result = [Bag]).

% ocl_bag_asOrderedSet( +Bag, -Result ) :- 
%    Return a Bag view

ocl_bag_asOrderedSet(Bag, Result) :- 
   ocl_bag_asSet(Bag, Result).

% ocl_bag_asSequence( +Bag, -Result ) :- 
%    Return a Sequence view of the bag
%    The order of elements is not specified

delay ocl_bag_asSequence(X, _) if var(X).
ocl_bag_asSequence(Bag, Result) :- 
   ( is_list(Bag) -> Result = Bag; Result = [Bag] ).

% ocl_bag_asBag( +Bag, -Result ) :- 
%    Return the Bag itself

delay ocl_bag_asBag(X, _) if var(X).
ocl_bag_asBag(Bag, Result) :- 
   ( is_list(Bag) -> Result = Bag; Result = [Bag] ).
       
%------------------------------------------------------------------------------
%
% Sequence - A sequential collection with random access and
%            potential duplicates.
%
%------------------------------------------------------------------------------

% Operations inherited from collection
ocl_seq_size(Seq, Size)               :- ocl_col_size(Seq, Size).
ocl_seq_includes(Seq, Obj, Result)    :- ocl_col_includes(Seq, Obj, Result).
ocl_seq_excludes(Seq, Obj, Result)    :- ocl_col_excludes(Seq, Obj, Result).
ocl_seq_count(Seq, Obj, N)            :- ocl_col_count(Seq, Obj, N).
ocl_seq_includesAll(Seq, Col, Result) :- ocl_col_includesAll(Seq, Col, Result).
ocl_seq_excludesAll(Seq, Col, Result) :- ocl_col_excludesAll(Seq, Col, Result).
ocl_seq_isEmpty(Seq, Result)          :- ocl_col_isEmpty(Seq, Result).
ocl_seq_notEmpty(Seq, Result)         :- ocl_col_notEmpty(Seq, Result).
ocl_seq_sum(Seq, N)                   :- ocl_col_sum(Seq, N).
ocl_seq_product(Seq, Col, Product)    :- ocl_col_product(Seq, Col, Product).        

% ocl_seq_equals( +Seq1, +Seq2, -Result ) :-
%    Result is 1 if Seq1 and Seq2 are equal (same elements and same order)
%    and 0 otherwise

delay ocl_seq_equals(X, _, _) if nonground(X).
delay ocl_seq_equals(_, Y, _) if nonground(Y).
ocl_seq_equals(Seq1, Seq2, Result) :- 
   ( Seq1 = Seq2 -> Result = 1; Result = 0).
 
% ocl_seq_union( +Seq1, +Seq2, -Result ) :-
%    Result is the sequence formed by all the elements of Seq1 followed by
%    the elements of Seqs

delay ocl_seq_union(X, _, _) if var(X).
delay ocl_seq_union(_, Y, _) if var(Y).
ocl_seq_union(Seq1, Seq2, Result) :- 
   append(Seq1, Seq2, Result).

% ocl_seq_flatten( +Seq, -Result ) :- 
%    Flattens the elements in Seq if they are collections
%
% WARNING: this operation always flattens the elements in the list. If the list
% holds other types of data that are stored as Prolog lists, the results of 
% these operations are impredictable.

delay ocl_seq_flatten(X, _) if var(X).
ocl_seq_flatten(Seq, Result) :- 
   flatten(Seq, Result). 

% ocl_seq_append( +Seq, +Obj, -Result ) :- 
%    Result is the sequence computed by adding Obj at the end of Seq

delay ocl_seq_append(X, _, _) if var(X).
ocl_seq_append(Seq, Obj, Result) :- 
   append( Seq, [Obj], Result ).

% ocl_seq_prepend( +Seq, +Obj, -Result ) :- 
%    Result is the sequence computed by adding Obj at the beginning of Seq

delay ocl_seq_prepend(X, _, _) if var(X).
ocl_seq_prepend(Seq, Obj, Result) :- 
   Result = [Obj|Seq].

% ocl_seq_insertAt( +Seq, +Obj, +Idx, -Result ) :- 
%    Result is the sequence computed by inserting Obj into Seq in position Idx
%    The behavior of this operation is not specified if the index Idx does not exist
%    exist in the sequence. ASSUMPTION: if Idx is larger than the size of the list
%    then the element is inserted at the end of the list.

delay ocl_seq_insertAt(X, _, _, _) if var(X).
delay ocl_seq_insertAt(_, _, Y, _) if nonground(Y).
ocl_seq_insertAt([], Obj, _, Result)  :-        
       ocl_mustBeObject(Obj, Object),
       Result = [Object], !.
ocl_seq_insertAt(Seq, Obj, 1, Result) :- 
       ocl_mustBeObject(Obj, Object),
       Result = [Object|Seq], !.
ocl_seq_insertAt([First|Rest],Obj,X,Result) :-
       ocl_mustBeObject(Obj, Object),
       X > 1, !,
       N is X - 1,
       ocl_seq_insertAt(Rest, Object, N, Aux),
       Result = [First|Aux].

% ocl_seq_subSequence( +Seq, +Lower, +Upper, -Result ) :- 
%    Result is the subsequence formed by the elemnents of Seq in positions between
%    Lower and Upper. According to the precondition 1 <= Lower <= Upper <= size(Seq)

delay ocl_seq_subSequence(X, _, _, _) if var(X).
delay ocl_seq_subSequence(_, Y, _, _) if nonground(Y).
delay ocl_seq_subSequence(_, _, Z, _) if nonground(Z).
ocl_seq_subSequence(Seq, X, X, Result) :-   
       !,
       nth1(X, Seq, Aux),
       Result = [Aux].
ocl_seq_subSequence(Seq, Lower, Upper, Result) :-
       Lower < Upper, !,
       N is Lower + 1,
       ocl_seq_subSequence(Seq, N, Upper, AuxList),
       nth1(Lower, Seq, AuxElem),
       Result = [AuxElem|AuxList].               
%Alternative implementation (to be tested)
%ocl_seq_subSequence(Seq, Lower, Upper, Result)
%   % Remove the elements after Upper
%   ( foreach(Elem, Seq),
%     foreach(Elem, AuxList),
%     count(_, 1, Upper) do true 
%   ),
%   % Remove the elements before Lower
%   ( Lower = 1 -> 
%     Result = AuxList;
%     ( fromto(AuxList, [First|Rest], Rest, Result),
%       count(_, 1, Lower) do true 
%     )
%   ).
   

% ocl_seq_ at( +Seq, +Idx, -Result ) :- 
%    Result is the element within Seq in position Idx

delay ocl_seq_at(X, _, _) if var(X).
delay ocl_seq_at(_, Y, _) if nonground(Y).
ocl_seq_at(Seq, Idx, Result) :- 
   nth1(Idx, Seq, Result).

% ocl_seq_indexOf( +Seq, +Obj, -Result ) :- 
%    Return a position in Seq that contains Obj
%    The exact position being returned is undefined (we will return the 
%    first). If the object is not found, the behavior is also undefined 

delay ocl_seq_indexOf(X, _, _) if nonground(X).
delay ocl_seq_indexOf(_, Y, _) if nonground(Y).
ocl_seq_indexOf( [], _, _ ) :- fail.
ocl_seq_indexOf( [Object|_], Obj, 1) :- 
       ocl_mustBeObject(Obj, Object),
       !.
ocl_seq_indexOf( [First|Rest], Obj, Result) :- 
       ocl_mustBeObject(Obj, Object),
       First \= Object, !,
       ocl_seq_indexOf( Rest, Obj, N ),
       Result is N + 1.
                
% ocl_seq_first( +Seq, -Result ) :- 
%    Return the first element in the sequence SEQ
%    Behavior is not specified if the sequence is empty

delay ocl_seq_first(X, _) if var(X).
ocl_seq_first([], _) :- fail.
ocl_seq_first([X|_], X) :- !.

% ocl_seq_last( +Seq, -Result ) :- 
%    Return the last element in the sequence SEQ
%    Behavior is not specified if the sequence is empty

delay ocl_seq_last(X, _) if var(X).
ocl_seq_last([], _ ) :- fail.
ocl_seq_last([First|Rest], Result) :-
   length([First|Rest], N),
   nth1([First|Rest], N, Result).
       
% ocl_seq_including( +Seq, +Obj, -Result ) :- 
%   Result is the sequence computed by adding Obj at the end of Seq
%   This operation is equivalent to append()

ocl_seq_including(Seq, Obj, Result) :- 
   ocl_mustBeObject(Obj, Object),
   ocl_seq_append(Seq, Object, Result).

% ocl_seq_excluding( +Seq, +Obj, -Result ) :- 
%    Remove all instances of Obj within Seq
%    This operation is equivalent to Bag.excluding

ocl_seq_excluding(Seq, Obj, Result) :- 
   ocl_mustBeObject(Obj, Object),
   ocl_bag_excluding(Seq, Object, Result).
       
% ocl_seq_asSet( +Seq, -Result ) :- 
%    Return a Set view of this sequence

delay ocl_seq_asSet(X, _) if nonground(X).
ocl_seq_asSet(Seq, Result) :- 
   ( is_list(Seq) -> list_to_ord_set(Seq, Result); Result = [Seq] ).

% ocl_seq_asSequence( +Seq, -Result ) :- 
%    Return the same sequence

delay ocl_seq_asSequence(X, _) if var(X).
ocl_seq_asSequence(Seq, Result) :- 
   ( is_list(Seq) -> Result = Seq; Result = [Seq] ).

% ocl_seq_asBag( +Seq, -Result ) :- 
%    Return a Bag view of this sequence

delay ocl_seq_asBag(X, _) if var(X).
ocl_seq_asBag(Seq, Result) :- 
   ( is_list(Seq) -> Result = Seq; Result = [Seq] ).

% ocl_seq_asOrderedSet( +Seq, -Result ) :- 
%    Return an OrderdedSet view of this sequence by removing duplicates 
%    and keeping the relative order among the remaining elements

delay ocl_seq_asOrderedSet(X, _) if nonground(X).
ocl_seq_asOrderedSet([], []) :- !.
ocl_seq_asOrderedSet(X, [X]) :- not is_list(X), !.
ocl_seq_asOrderedSet([First|Rest],Result) :-
   ocl_seq_asOrderedSet(Rest, Aux),
   ( ocl_seq_includes(Rest, First, 1) ->
     Result = Aux; 
     Result = [First|Aux] 
   ).

%------------------------------------------------------------------------------
%
% OrderedSet - A sequence without duplicates.
%
%------------------------------------------------------------------------------

% Operations inherited from collection
ocl_ordset_size(Set, Size)            :- ocl_col_size(Set, Size).
ocl_ordset_includes(Set, Obj)         :- ocl_col_includes(Set, Obj).
ocl_ordset_excludes(Set, Obj)         :- ocl_col_excludes(Set, Obj).
ocl_ordset_count(Set, Obj, N)         :- ocl_col_count(Set, Obj, N).
ocl_ordset_includesAll(Set, Col, Result) :- 
                                      ocl_col_includesAll(Set, Col, Result).
ocl_ordset_excludesAll(Set, Col, Result) :- 
                                      ocl_col_excludesAll(Set, Col, Result).
ocl_ordset_isEmpty(Set, Result)       :- ocl_col_isEmpty(Set, Result).
ocl_ordset_notEmpty(Set, Result)      :- ocl_col_notEmpty(Set, Result).
ocl_ordset_sum(Set, N)                :- ocl_col_sum(Set, N).
ocl_ordset_product(Set, Col, Product) :- ocl_col_product(Set, Col, Product).

% ocl_ordset_append( +Ordset, +Obj, -Result) :- 
%    If Obj is not in Ordset, Result has all the elements of OrdSet plus Obj
%    at the end. Otherwise, Result is equal to OrdSet

delay ocl_ordset_append(X,_,_) if nonground(X).
delay ocl_ordset_append(_,Y,_) if nonground(Y).
ocl_ordset_append(OrdSet, Obj, Result) :- 
   ocl_mustBeObject(Obj, Object),
   ( ocl_ordset_includes(OrdSet, Object, 1) ->
     Result = OrdSet;
     append(OrdSet, [Object], Result)
   ).

% ocl_ordset_prepend( +OrdSet, +Obj, -Result ) :- 
%    If Obj is not in Ordset, Result has all the elements of OrdSet plus Obj
%    at the beginning. Otherwise, Result is equal to OrdSet

delay ocl_ordset_prepend(X,_,_) if nonground(X).
delay ocl_ordset_prepend(_,Y,_) if nonground(Y).
ocl_ordset_prepend(OrdSet, Obj, Result) :- 
   ocl_mustBeObject(Obj, Object),
   ( ocl_ordset_includes(OrdSet, Object, 1) ->
     Result = OrdSet;
     Result = [Object|OrdSet]
   ).
            
% ocl_ordset_insertAt( +OrdSet, +Obj, +Idx, -Result) :- 
%    If Obj is in OrdSet, Result is equal to OrdSet
%    Otherwise, Result is computed by inserting Obj in position Idx of OrdSet

delay ocl_ordset_insertAt(X, _, _, _) if nonground(X).
delay ocl_ordset_insertAt(_, Y, _, _) if nonground(Y).
delay ocl_ordset_insertAt(_, _, Z, _) if nonground(Z).
ocl_ordset_insertAt(OrdSet, Obj, Idx, Result) :-
   ocl_mustBeObject(Obj, Object),
   ( ocl_ordset_inclues(OrdSet, Object, 1) ->
     Result = OrdSet;
     ocl_seq_insertAt(OrdSet, Object, Idx, Result)
   ).

% ocl_ordset_subOrderedSet( +OrdSet, +Lower, +Upper, -Result ) :-
%    Returns the subset between positions Lower and Upper within the set
%    According to the precondition 1 <= Lower <= Upper <= size(Seq)

ocl_ordset_subOrderedSet(OrdSet, Lower, Upper, Result) :-
   ocl_seq_subSequence(OrdSet, Lower, Upper, Result).

% ocl_ordset_at( +OrdSet, +Idx, -Result ) :- 
%    Return the element with position IDX within the set OrdSet
%    According to the precondition, 1 <= Idx <= size(Seq)

ocl_ordset_at(OrdSet, Idx, Result) :-
   ocl_seq_at(OrdSet, Idx, Result).
       
% ocl_ordset_first( +OrdSet, -Result ) :- 
%    Return the first element of the ordered set

ocl_ordset_first(OrdSet, Result) :-       
   ocl_seq_first(OrdSet, Result).
       
% ocl_ordset_last( +OrdSet, +Result ) :- 
%    Return the last element of the ordered set
ocl_ordset_last(OrdSet, Result) :-
   ocl_seq_last(OrdSet, Result).
       
       
       

              
