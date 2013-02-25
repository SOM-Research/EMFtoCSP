%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%              A lightweight string solver                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Author: Fabian Büttner (fabian.buettner@inria.fr)
% (c) 2012,2013
% See "Lightweight String Reasoning in Model Finding", to appear in Sosym


:- lib(ic).
:- lib(ech).
:- lib(ordset).
:- handler string.

:- option(already_in_store, off). 
:- option(check_guard_bindings, off).
:- option(default_chr_priority,6).


:- constraints 
        str_eq/3,str_eqNX/4,str_eqNXY/5,
        str_domain/2,
        str_domain_hole/2,
	str_len/2, 
        str_outof/3,
        str_concat/3,str_concatNX/4,str_concatNXY/5,str_concatNXYZ/6,
	str_substr/4,str_substrNX/5,str_substrNXY/6,
	str_indexof/3, str_indexofNX/4, str_indexofNXY/5, 
        str_contains_substring/3.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                    INIT AND HELPER RULES                             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

build_eq_1          ::= str_len(X,NX) \ str_eq(X,Y,R) <=> str_eqNX(X,Y,R,NX).
build_eq_2          ::= str_len(Y,NY) \ str_eqNX(X,Y,R,NX) <=> str_eqNXY(X,Y,R,NX,NY), R #:: 0..1.

build_concat_1      ::= str_len(X,NX) \ str_concat(X,Y,Z) <=> str_concatNX(X,Y,Z,NX).
build_concat_2      ::= str_len(Y,NY) \ str_concatNX(X,Y,Z,NX) <=>  str_concatNXY(X,Y,Z,NX,NY).
build_concat_3      ::= str_len(Z,NZ) \ str_concatNXY(X,Y,Z,NX,NY) <=> str_concatNXYZ(X,Y,Z,NX,NY,NZ).

build_indexof_1     ::= str_len(X,NX) \ str_indexof(X,Y,I) <=> str_indexofNX(X,Y,I,NX).
build_indexof_2     ::= str_len(Y,NY) \ str_indexofNX(X,Y,I,NX) <=> str_indexofNXY(X,Y,I,NX,NY).

build_substr_1      ::= str_len(X,NX) \ str_substr(X,I,J,Y) <=> str_substrNX(X,I,J,Y,NX).
build_substr_2      ::= str_len(Y,NY) \ str_substrNX(X,I,J,Y,NX) <=> str_substrNXY(X,I,J,Y,NX,NY).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                    SYMBOLIC INFERENCE RULES                          %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
sym_eq_reflexive     ::= str_eqNXY(X,X,R,NX,NY) <=> print("sym_eq_reflexive"),nl,R = 1.


%expensive
%sym_eq_nothird       ::= str_eqNXY(X,Y,R1,NX,NY) \ str_eqNXY(X,Y,R2,NX,NY) <=> R1 #= R2.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                    LENGTH INFERENCE RULES                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

length_range          ::= str_len(X,NX) ==> NX :: 0..5000.

length_unify          ::= str_len(X,N1) \ str_len(X,N2) <=> N1 #= N2.

length_ground         ::= str_len(X,N) ==> is_list(X) | length(X,N).

length_empty_eq       ::= str_eqNXY(X,Y,R,NX,NY) ==>  ic:and(NX #= 0, NY #= 0) => R #= 1.

length_eq_length      ::= str_eqNXY(X,Y,R,NX,NY) ==> R #= 1 => NX #= NY.

length_indexof        ::= str_indexofNXY(X,Y,I,NX,NY) ==> I #>= 0, I #>= 1 => I + NY - 1 #=< NX, I #>= 1 => NX #>= NY, I #>= 1 => NX #>= 1.

length_concat         ::= str_concatNXYZ(X,Y,Z,NX,NY,NZ) ==> NZ #= NX + NY, NX #>= 0,  NY #>=0.

length_substr         ::= str_substrNXY(X,I,J,Y,NX,NY) ==> NY #= (J - I + 1), NX#>=J, I#>=1, J#>=I.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                    DOMAIN INFERENCE RULES                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% on unification, intersect the domains
domain_unify         ::= str_domain(S,D1), str_domain(S,D2) <=> ordset:ord_intersect(D1,D2,D), str_domain(S,D).

% a domain cannot be empty
domain_empty         ::= str_domain(S,[]) <=> fail.

% a singleton domain creates a ground string
domain_singleton     ::= str_domain(S,[Val]) <=> S=Val.

% a ground string checks and removes the domain
domain_ground        ::= str_domain(S,D) <=> ground(S) | ord_memberchk(S,D).

% a hole reduces a domain
domain_hole          ::= str_domain_hole(S,Value), str_domain(S,D1)  <=> ord_del_element(D1,Value,D2), D1\=D2 | str_domain(S,D2).

% a hole checks a ground string
domain_hole_ground   ::= str_domain_hole(S,Val) <=> ground(S) | S \= Val.

domain_length_ground ::= 
    str_len(S,N) \ str_domain(S,D1) <=> 
    ground(N), str_domain_retain_length(D1,N,D2), D1\=D2 | str_domain(S,D2).

% OPTIONAL RULE: EXPENSIVE (DUE TO THE CHR IMPLEMENTATION)
% domain_length_strong_propagation ::= str_domain(S,D), str_len(S,L) ==> str_domain_collect_lengths(D,Lengths), L :: Lengths.


str_domain_collect_lengths(D,Lengths) :-
        (foreach(E,D),fromto([],In,Out,Lengths) do
            length(E,N),
            ordset:ord_add_element(In,N,Out)).


domain_eq_ground_f    ::= str_eqNXY(X,Y,0,NX,NY) <=> ground([X,Y]) | X\=Y.
domain_eq_ground_t    ::= str_eqNXY(X,Y,1,NX,NY) <=> ground([X,Y]) | X=Y.
domain_eq_ground_var  ::= str_eqNXY(X,Y,R,NX,NY) <=> is_list(X),is_list(Y),var(R) | str_unfold_eq(X,Y,R).

% OPTIONAL RULE: EXPENSIVE (DUE TO THE CHR IMPLEMENTATION)
domain_neq_propagate1   ::= str_eqNXY(X,Y,0,NX,NY) \ str_domain(X,D) <=> ground(Y),ordset:ord_del_element(D,Y,D1),D\=D1 | str_domain(X,D1).
domain_neq_propagate2   ::= str_eqNXY(X,Y,0,NX,NY) \ str_domain(Y,D) <=> ground(X),ordset:ord_del_element(D,X,D1),D\=D1 | str_domain(Y,D1).

domain_gen_contains        ::= str_indexofNXY(X,Y,I,NX,NY) ==> I #= 0 => R #= 0, I#\=0 => R #= 1, str_contains_substring(X,Y,R). 

domain_contains_f     ::= 
    str_contains_substring(X,Y,0), str_domain(X,D) <=> 
    ground(Y),str_domain_remove_all_substrings(D,Y,D2),D \= D2 | str_domain(X,D2).

domain_contains_t  ::= 
    str_contains_substring(X,Y,1), str_domain(X,D) <=> ground(Y),str_domain_retain_all_substrings(D,Y,D2),D \= D2 | length(D,L1),length(D2,L2),str_domain(X,D2).

domain_ground_str_contains ::= str_contains_substring(X,Y,R) <=> ground([X,Y]) | true.

domain_indexof_groundXY ::= 
    str_indexofNXY(X,Y,I,NX,NY) <=> is_list(X),is_list(Y),ground(NX),ground(NY) | str_unfold_indexof(X,Y,I).

domain_indexof_groundYI ::= 
    str_indexofNXY(X,Y,I,NX,NY) \ str_domain(X,D1) <=> 
    ground(Y), ground(I), I\=0, str_domain_retain_all_substrings_at_idx(D1,I,Y,D2), D1 \= D2 | str_domain(X,D2).

domain_concat_inst1 ::= str_concatNXYZ(X,Y,Z,NX,NY,NZ) <=> is_list(X),is_list(Y) | append(X,Y,Z).
domain_concat_inst2 ::= str_concatNXYZ(X,Y,Z,NX,NY,NZ) <=> is_list(X),is_list(Z) | append(X,Y,Z).
domain_concat_inst3 ::= str_concatNXYZ(X,Y,Z,NX,NY,NZ) <=> is_list(Y),is_list(Z) | append(X,Y,Z).

% NOTE(concat): we could add a domain inference rule:
%domain_concat_groundX          ::= str_concat(X,Y,Z), str_domain(Z,D), str_domain( | ground(X) 

domain_substr_groundXIJ ::= str_substrNXY(X,I,J,Y,NX,NY) <=> is_list(X),ground(I),ground(J) | N is J - I + 1, sublist(X,I,N,Y).

domain_substr_groundXY  ::= str_substrNXY(X,I,J,Y,NX,NY) <=> is_list(X),is_list(Y) | str_unfold_substr(X,I,J,Y).

domain_substr_groundYIJ  ::= 
    str_substrNXY(X,I,J,Y,NX,NY) \ str_domain(X,D1) <=>
    ground([Y,I,J]), str_domain_retain_all_substrings_at_idx(D1,I,Y,D2), D1 \= D2 | str_domain(X,D2).
    
domain_substr_groundY  ::= 
    str_substrNXY(X,I,J,Y,NX,NY) \ str_domain(X,D1) <=>
    ground(Y), str_domain_retain_all_substrings(D1,I,Y,D2), D1 \= D2 | str_domain(X,D2).
                                                    
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                          ALLDIFFERENT RULES                          %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
alldifferent_outof    ::= str_outof(X, Left, Right) <=> ground(X) | str_exclude_value(Left, X),str_exclude_value(Right, X).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%             OLD RULES AND EXPERIMENTAL RULES (REMOVED)               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


% indexofdomain3       ::= str_indexofNXY(X,Y,I,NX,NY), str_get_domain(X,D) <=> ground(Y),var(I),str_indexof_checkdomain(X,Y,B1,B2)  | print("check"),nl,,B1 #= 0 => I #= 0, B2 #= 1 => I #\= 0.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                    HELPERS USED IN THE CHRS                          %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

:- mode str_domain_remove_all_substrings(++,++,-).
str_domain_remove_all_substrings(D1,V,D2) :-
%        print("str_domain_remove_all_substrings"),nl,
        (foreach(E,D1),fromto([],In,Out,D2),param(V) do 
            (subset(V,E) -> Out = In; append(In,[E],Out))).
            
:- mode str_domain_retain_all_substrings(++,++,-).
str_domain_retain_all_substrings(D1,V,D2) :-
%        print("str_domain_retain_all_substrings"),nl,
        (foreach(E,D1),fromto([],In,Out,D2),param(V) do 
            (subset(V,E) -> append(In,[E],Out);Out = In)).
            
:- mode str_domain_retain_all_substrings_at_idx(++,++,++,-).
str_domain_retain_all_substrings_at_idx(D1,I,V,D2)  :- 
%        print("str_domain_retain_all_substrings_at_idx"),nl,
        length(V,NV),
        (foreach(E,D1),fromto([],In,Out,D2),param(V,NV,I) do 
            (sublist(E,I,NV,V) -> append(In,[E],Out);Out = In)).

:- mode str_domain_retain_length(++,++,-).
str_domain_retain_length(D1,L,D2) :-
        (foreach(E,D1),fromto([],In,Out,D2),param(L) do 
            (length(E,L) -> append(In,[E],Out);Out = In)).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                          LABELING                                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

str_label_one :-
        chr_get_constraint(str_domain(S,D)),
        !,
        member(S1,D),
        S=S1.

str_label_one.


str_labeling :- str_labeling(48..122,indomain),!.

str_labeling(AlphabetRange, Method) :-
        str_label_domains,
        str_label_lengths(AlphabetRange, Method, Cs),
        ic:search(Cs,0,input_order, Method, bbs(10000), []).

str_label_domains :-
        chr_get_constraint(str_domain(S,D)),
        !,
        member(S1,D),
%        printf("Labeling %w with %w", [SN,S1]),nl,
        S=S1,
        str_label_domains.

str_label_domains.

str_label_lengths(AlphabetRange, Method, Cs) :-
        chr_get_constraint(str_len(S,N)),
%        printf("got %w", [chr_get_constraint(str_len(S,N))]),nl,
        !,
        ic:indomain(N),
        length(S,N),
        S :: AlphabetRange,
%        printf("Using %w", [N]),nl,
        str_label_lengths(AlphabetRange, Method, Cs1),
        append(S,Cs1,Cs).


str_label_lengths(_,_,[]).


call_with_errmsg(Goal) :-
        call(Goal),!.

call_with_errmsg(Goal) :-
        printf("FAILED: %w",[Goal]),nl,
        fail.

:- mode str_restrict_to_chars(+).
str_restrict_to_chars([], _CharacterRange) :- !.
str_restrict_to_chars(Cs, CharacterRange) :- Cs #:: CharacterRange.

str_set_lengths_and_label(Method,CharacterRange) :-
	str_set_lengths(Chars,Method,CharacterRange),
	str_label_chars(Chars).

:- mode str_label_chars(+).
str_label_chars([]) :- !.

str_label_chars(Cs) :-
 	search(Cs,0,input_order, indomain_random, bbs(0), []).

:- mode str_label_chars(+,-).
str_label_chars(Cs,Backtracks) :-
 	search(Cs,0,input_order, indomain_random, bbs(0), [backtrack(Backtracks)]).

:- mode str_collect_lengths(+,-).
str_collect_lengths(In,Out) :-
	chr_get_constraint(str_len(X,N)),
	!,
	str_collect_lengths([str_len(X,N)|In],Out).
str_collect_lengths(X,X).


% str_process_eqs :-
%         chr_get_constraint(str_eqNXY(X,Y,R,_NX,_NY)),
% 	!,
% 	str_unfold_eq(X,Y,R),
% 	str_process_eqs.
% str_process_eqs.

str_unfold_eq(X,Y,R) :-
        length(X,N),
        length(Y,M),
        ( R == 1 -> 
            X = Y; 
            ( N\=M -> 
                R=0; 
                (foreach(X1,X),foreach(Y1,Y),fromto(1,In,Out,R) do 
                    ic:and(X1 #= Y1,In,Out)))).

str_unfold_substr(X,I,_,Y) :-
	length(X,NX),
	length(Y,NY),
	LengthDiff is NX - NY, 
        % iterate over all possible offsets 0..(NX - NY)
	( count(Offset,0,LengthDiff),param(I,X,Y,NY) do
            % iterate over the lengths of NY
            (count(K,1,NY),fromto(1,In,Out,R1),param(Offset,X,Y) do
               L is K + Offset, 
	       listut:nth1(L,X,Z1), 
	       listut:nth1(K,Y,Z2), 
  	       ic:and(In, Z1 #= Z2, Out)
            ),
	    I #= Offset + 1 => R1 #= 1
	).
	    


str_unfold_indexof(X,Y,P) :-
	length(X,NX),
	length(Y,NY),
	R2 #:: 0..1,
	P #>= 0,
	LengthDiff is NX - NY, 
	(count(Offset,0,LengthDiff),param(P,Y,X,NY),fromto(0,In1,Out1,R2) do 
            % R1 : X contains Y at Offset
            (count(I,1,NY),fromto(1,In,Out,R1),param(Y,X,Offset) do
                J is I + Offset, 
                listut:nth1(I,Y,Z1),
                listut:nth1(J,X,Z2),
                ic:and(In, Z1 #= Z2, Out)
            ),
            ic:and(In1 #= 0, R1 #= 1) #= (P #= Offset + 1),
            ic:or(In1,R1,Out1)
        ),
        (P #= 0) #= (R2 #= 0). 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                         VARIOUS HELPERS                              %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
str_trace(Text,Vars) :- printf(Text,Vars),nl.


str_alldifferent(List) :-
	str_alldifferent(List, []).
str_alldifferent([], _Left).

str_alldifferent([X | Right], Left) :-
	str_outof(X, Left, Right),
	str_alldifferent(Right, [X | Left]).

str_exclude_value([], _N).
str_exclude_value([X | Xs], N) :-
	str_domain_hole(X, N),
        str_exclude_value(Xs, N).

:- mode sublist(++,++,++,-).
sublist(L,I,N,Res) :-
        S is I + N - 1,
        (count(J,I,S),foreach(C,Res),param(L) do listut:nth1(J,L,C)).

% translates a string atom into a sequence of character numbers
str_to_char_list(Str,L) :-
        string_length(Str,N),
        (count(I,1,N),foreach(C,L),param(Str) do string_code(Str,I,C)).

% constrains a string variable, using a list of string atoms
str_constrain_str_domain(Str,ListOfStrings) :-
        (foreach(Str1,ListOfStrings),foreach(Chars1,ListOfChars) do str_to_char_list(Str1,Chars1), str_len(Chars1,_)),
        sort(ListOfChars,ListOfCharsSorted),
        str_domain(Str,ListOfCharsSorted).
