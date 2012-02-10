%

% OCL Standard Library - Predefined Iterators for Collections

% 



% 2007/01/31:

%

% Operations that do not adhere to the standard

% - Collection.collect() does not work properly so far,

%   as it is based on flatten()

% Undefined behaviors in the standard

% - The element returned by Collection.any() is chosen arbitrarily



% 2007/03/20:

% - Fully operational

%   * ocl_col_one, ocl_col_forAll, ocl_col_exists, ocl_col_isUnique, ocl_col_any

%   * ocl_set_select, ocl_set_reject

% - To be fixed

%   * forAll and exists can be implemented with "and" or "or" instead of the

%     sum of N variables. It is not clear whether the efficiency of the method

%     will improve or not

% - Missing operators

%   * collect, collectNested



%

% ECLiPSe libraries

%

:- lib(ic).             % Interval constraints

:- lib(apply).          % Apply predicates to lists

:- lib(apply_macros).   % Predefined applications of predicates 

:- lib(lists).          % Operations on lists



%------------------------------------------------------------------------------

% Collection iterators

%------------------------------------------------------------------------------

%    Test if Predicate holds in at least one element of Collection
delay ocl_col_exists(_,_,Collection,_,_) if var(Collection).
ocl_col_exists(Instances, Vars, Collection, Predicate, Result ) :-
	Result::0..1,
	property_apply(Instances, Vars, Collection, Predicate, TruthValues),
	( foreach(TV,TruthValues),fromto(0,In,Out,Result) do ic:or(In,TV,Out)).



%    Test if Predicate holds in all elements of Collection
delay ocl_col_forAll(_,_,Collection,_,_) if var(Collection).
ocl_col_forAll(Instances, Vars, Collection, Predicate, Result ) :-
	Result::0..1,
	property_apply(Instances, Vars, Collection, Predicate, TruthValues),
	( foreach(TV,TruthValues),fromto(1,In,Out,Result) do ic:and(In,TV,Out)).



% ocl_col_isUnique(Instances, Vars, Collection, Predicate, Result) :-

%    Test if applying Predicate to Collection produces a list

%    with unique values



ocl_col_isUnique(Instances, Vars, Collection, Predicate, Result ) :-

  Result::0..1,

  property_apply(Instances, Vars, Collection, Predicate, List),  

  list_is_unique(List, Result).

                               

% ocl_col_any(Instances, Vars, Collection, Predicate, Result) :-

%    Result is any element of Collection which satisfies 

%    Predicate.                                       



delay ocl_col_any(_, _, X, _, _) if var(X).

ocl_col_any(_, _, [], _, ocl_undef).

ocl_col_any(Instances, Vars, [H|T], Predicate, Result) :-

  apply( Predicate, [Instances, [H|Vars], Value] ),

  aux_find_first_success( Instances, Vars, [H|T], Predicate, Value, Result ).



delay aux_find_first_success(_, _, _, _, X, _) if var(X).

aux_find_first_success( _, _, [H|_], _, 1, H ).

aux_find_first_success( Instances, Vars, [_|T], Predicate, 0, Result ) :-

  ocl_col_any(Instances, Vars, T, Predicate, Result ).



% ocl_one(Collection, Predicate) :-

%    Test if there is only one element in Collection which

%    satisfies Predicate



ocl_col_one(Instances, Vars, Collection, Predicate, Result ) :-

   property_sat_count(Instances, Vars, Collection, Predicate, N),

   #=(N, 1, Result).

   

% ocl_T_collect(Instances, Vars, Collection, Predicate, Result) :-

%    Result is the Collection of elements produced by applying

%    Predicate to all elements of the collection and flattening

%    them. Behavior depends on the collection type.



ocl_set_collect(Instances, Vars, Set, Predicate, Result) :- 

    ocl_set_collectNested(Instances, Vars, Set, Predicate, X),

    ocl_bag_flatten(X, Result).

ocl_bag_collect(Instances, Vars, Bag, Predicate, Result) :- 

    ocl_bag_collectNested(Instances, Vars, Bag, Predicate, X),

    ocl_bag_flatten(X, Result).

ocl_seq_collect(Instances, Vars, Seq, Predicate, Result) :- 

    ocl_seq_collectNested(Instances, Vars, Seq, Predicate, X),

    ocl_seq_flatten(X, Result).



%------------------------------------------------------------------------------

% Set iterators

%------------------------------------------------------------------------------



% ocl_set_select(Instances, Vars, Set, Predicate, Result) :-

%    Result is the subset of elements in Set that satisfy Predicate



ocl_set_select(Instances, Vars, Set, Predicate, Result) :-

   property_apply(Instances, Vars, Set, Predicate, List),

   aux_col_select(Set, List, Result).



% ocl_set_reject(Instances, Vars, Set, Predicate, Result) :-

%    Result is the subset of elements in Bag that do not satisfy Predicate



ocl_set_reject(Instances, Vars, Set, Predicate, Result) :-

   property_apply(Instances, Vars, Set, Predicate, List),

   aux_col_reject(Set, List, Result).



% ocl_set_collectNested(Instances, Vars, Set, Predicate, Result) :-

%    Result is the set produced by applying Predicate to all elements in 

%    the Set



ocl_set_collectNested(Instances, Vars, Set, Predicate, Result) :- 

   property_apply(Instances, Vars, Set, Predicate, Aux),

   suspend(sort(Aux, Result), 0, Aux->inst).



% ocl_set_sortedBy(Instances, Vars, Set, Predicate, Result) :-

%    Result is the OrderedSet computed by ordering the elements of Set

%    in increasing order according to the values of Predicate



ocl_set_sortedBy(Instances, Vars, Set, Predicate, Result) :- 

   qsort_apply(Instances, Vars, Set, Predicate, Result).



%------------------------------------------------------------------------------

% Bag iterators

%------------------------------------------------------------------------------



% ocl_bag_select(Instances, Vars, Bag, Predicate, Result) :-

%    Result is the subbag of elements in Bag that satisfy Predicate



ocl_bag_select(Instances, Vars, Bag, Predicate, Result) :-

   property_apply(Instances, Vars, Bag, Predicate, List),

   aux_col_select(Bag, List, Result).



% ocl_bag_reject(Instances, Vars, Bag, Predicate, Result) :-

%    Result is the subbag of elements in Bag that do not satisfy Predicate



ocl_bag_reject(Instances, Vars, Bag, Predicate, Result) :-

   property_apply(Instances, Vars, Bag, Predicate, List),

   aux_col_reject(Bag, List, Result).



% ocl_bag_collectNested(Instances, Vars, Bag, Predicate, Result) :-

%    Result is the bag produced by applying Predicate to all elements in 

%    the Bag



ocl_bag_collectNested(Instances, Vars, Bag, Predicate, Result) :- 

   property_apply(Instances, Vars, Bag, Predicate, Result).



% ocl_bag_sortedBy(Instances, Vars, Bag, Predicate, Result) :-

%    Result is the Sequence computed by ordering the elements of Bag

%    in increasing order according to the values of Predicate



ocl_bag_sortedBy(Instances, Vars, Bag, Predicate, Result) :- 

   qsort_apply(Instances, Vars, Bag, Predicate, Result).



%------------------------------------------------------------------------------

% Sequence iterators

%------------------------------------------------------------------------------



% ocl_seq_select(Instances, Vars, Seq, Predicate, Result) :-

%    Result is the subsequence of elements in Seq that satisfy Predicate

%    The relative order among elements should be preserved.



ocl_seq_select(Instances, Vars, Seq, Predicate, Result) :-

   property_apply(Instances, Vars, Seq, Predicate, List),

   aux_col_select(Seq, List, Result).



% ocl_seq_reject(Instances, Vars, Seq, Predicate, Result) :-

%    Result is the subsequence of elements in Seq that do not satisfy 

%    Predicate.

%    The relative order among elements should be preserved.



ocl_seq_reject(Instances, Vars, Seq, Predicate, Result) :-

   property_apply(Instances, Vars, Seq, Predicate, List),

   aux_col_reject(Seq, List, Result).



% ocl_seq_collectNested(Seq, Predicate, Result) :-

%    Result is the sequence produced by applying Predicate to all 

%    elements in Seq



ocl_seq_collectNested(Instances, Vars, Seq, Predicate, Result) :- 

   property_apply(Instances, Vars, Seq, Predicate, Result).



% ocl_seq_sortedBy(Seq, Predicate, Result) :-

%    Result is the Sequence computed by ordering the elements of Seq

%    in increasing order according to the values of Predicate



ocl_seq_sortedBy(Instances, Vars, Seq, Predicate, Result) :- 

   qsort_apply(Instances, Vars, Seq, Predicate, Result).



%------------------------------------------------------------------------------

% Auxiliary predicates

%------------------------------------------------------------------------------



% property_apply(Instances, Vars, Collection, Property, Result) :-

%    Compute the list produced by applying the property to

%    all elements of the collection. The list appears in the same

%    order as the elements in the original set.



delay property_apply(_,_,X,_,_) if var(X).

property_apply(Instances, Vars, Collection, Property, Result) :- 

   ( 

     foreach(Elem, Collection),   

     foreach(Value, Result),

     param(Property, Instances, Vars)

     do

       apply(Property, [Instances, [Elem|Vars], Value]) 

   ). 

   

% property_sat_count(Instances, Vars, Collection, Property, Result) :-

%    Count the number of elements in the collection that satisfy

%    a given Property.



delay property_sat_count(_, _, X, _, _) if var(X).

property_sat_count(Instances, Vars, Collection, Property, Result ) :- 

   property_apply(Instances, Vars, Collection, Property, TruthValues),

   Result #= sum(TruthValues). 



% aux_col_select(Collection, List, Result) :-

%    Result is the subset of elements in the Collection where

%    the same element in the List is true



delay aux_col_select(X, _, _) if var(X).

delay aux_col_select(_, Y, _) if nonground(Y).

aux_col_select(Collection, List, Result) :-

  ( foreach(Elem,Collection),

    foreach(TruthValue,List),

    fromto(Result,In,Out,[])

    do

      ( TruthValue = 1 -> In = [Elem | Out] ; In = Out ) 

  ). 



% aux_col_reject(Collection, List, Result) :-

%    Result is the subset of elements in the Collection where

%    the same element in the List is false



delay aux_col_reject(X, _, _) if var(X).

delay aux_col_reject(_, Y, _) if nonground(Y).

aux_col_reject(Collection, List, Result) :-

  ( foreach(Elem,Collection),

    foreach(TruthValue,List),

    fromto(Result,In,Out,[])

    do

      ( TruthValue = 0 -> In = [Elem | Out] ; In = Out ) 

  ). 



% list_is_unique(List, Result) :-

%    Result is true if and only if the list does not have repeated

%    elements



delay list_is_unique(X, _) if nonground(X).

list_is_unique([], 1).

list_is_unique([H|T], X) :- (member(H,T) -> X = 0 ; list_is_unique(T,X)). 



% qsort_apply(Instances, Vars, List, Pred, Out) :-

%    Out is the sorted version of List. Elements are sorted in increasing

%    order according to the result of Predicate applied to each element.



qsort_apply(_, _, [], _, []).

qsort_apply(Instances, Vars,[H|T], Predicate, Y) :-

   part_apply(Instances, Vars, H, T, Predicate, Smaller, Larger),

   qsort_apply(Instances, Vars, Smaller, Predicate, S),

   qsort_apply(Instances, Vars, Larger, Predicate, L),

   append(S, [H|L], Y).



% part_apply(Instances, Vars, Elem, Rest, Pred, Smaller, Larger) :-

%    Partition the list Rest into two sublists, Smaller and Larger, having all

%    elements which are respectively <= and > in the order wrt Elem. The

%    order is based on the result of applying Pred to the elements of Rest.



part_apply(_, _, _, [], _, [], []).

part_apply(Instances, Vars, X, [Y|Xs], Pred, [Y|Ls],Bs) :-

    apply(Pred, [Instances, [X|Vars], S]),

    apply(Pred, [Instances, [Y|Vars], T]),

    S > T,

    part_apply(Instances, Vars, X, Xs, Pred, Ls, Bs).

part_apply(Instances, Vars, X, [Y|Xs], Pred, Ls, [Y|Bs]) :-

    apply(Pred, [Instances, [X|Vars], S]),

    apply(Pred, [Instances, [Y|Vars], T]),

    S =< T,

    part_apply(Instances, Vars, X, Xs, Pred, Ls, Bs).



