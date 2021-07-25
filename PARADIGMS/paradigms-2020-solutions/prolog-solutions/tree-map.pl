merge(nil, T, T) :- !.

merge(T, nil, T) :- !.

merge(tree(K1, V1, P1, L1, R1), tree(K2, V2, P2, L2, R2), tree(K1, V1, P1, L1, RT)) :-
    P1 < P2, merge(R1, tree(K2, V2, P2, L2, R2), RT).

merge(tree(K1, V1, P1, L1, R1), tree(K2, V2, P2, L2, R2), tree(K2, V2, P2, LT, R2)) :-
    P1 >= P2, merge(tree(K1, V1, P1, L1, R1), L2, LT).



split(nil, _, nil, nil, nil) :- !.

split(tree(K, V, P, L, R), X, tree(K, V, P, L, L1), M, Right) :-
    K < X, split(R, X, L1, M, Right), !.

split(tree(K, V, P, L, R), X, Left, M, tree(K, V, P, R1, R)) :-
    K > X, split(L, X, Left, M, R1), !.

split(tree(K, V, P, L, R), K, L, tree(K, V, P, nil, nil), R) :- !.



map_put(nil, K, V, tree(K, V, P, nil, nil)) :- rand_int(999999999, P), !.

map_put(T, K, V, Result) :-
    split(T, K, L, _, R),
    rand_int(999999999, P),
    merge(L, tree(K, V, P, nil, nil), M),
    merge(M, R, Result).



map_remove(T, K, Result) :-
    split(T, K , L, _, R),
    merge(L, R, Result).



map_get(T, K, V) :- split(T, K, _, tree(K, V, _, _, _), _).



map_replace(T, K, V, T) :-
    split(T, K, L, nil, R), !.

map_replace(T, K, V, Result) :-
    split(T, K, Left, tree(K1, V1, P, L, R), Right),
    merge(Left, tree(K, V, P, L, R), M),
    merge(M, Right, Result).



append([], T, T) :- !.

append([(K, V) | T], CurT, Result) :-
    map_put(CurT, K, V, R1),
    append(T, R1, Result).

map_build(List, Result) :-  append(List, nil, Result).