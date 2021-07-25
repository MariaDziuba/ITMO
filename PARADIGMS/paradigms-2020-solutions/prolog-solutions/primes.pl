if_div(N, K) :- 
	T is mod(N, K),
	0 = T.

next_divisor(N, X, X) :-
	X =< N,
	if_div(N, X),
	!.
	
next_divisor(N, X, D) :-
	X =< N,
	not(if_div(N, X)),
	X1 is X + 1,
	next_divisor(N, X1, D).

prime(N) :- 
	N > 1,
	next_divisor(N, 2, N).

composite(N) :-
	N > 1, 
	not(prime(N)).

prime_divisors(1, []) :- !.

prime_divisors(N, [N]) :- 
	prime(N), !.

min_prime(N, X, R) :- 
	X * X =< N, 
	if_div(N, X), 
	prime(X), 
	R is X; 
	X * X =< N, 
	X1 is X + 1, 
	min_prime(N, X1, R). 

if_sort([H]) :- !.

if_sort([H, H1 | T]) :- 
	H =< H1.

prime_divisors(N, [H | T]) :-
  	number(N),
	N > 1,
	min_prime(N, 2, H),
	N1 is div(N, H),
	prime_divisors(N1, T),
	if_sort([H | T]), !.

prime_divisors(N, [H | T]) :-
	not(number(N)),
	prime(H),
	prime_divisors(N1, T),
	if_sort([H | T]),
 	N is N1 * H, !.

unique_prime_divisors(N, D1):-
 	prime_divisors(N, D),!,
 	remove_copies(D,D1).

remove_copies([],[]):-!.

remove_copies([H | T], V):-
 	member(H, T),
	!,
 	remove_copies(T,V).

remove_copies([H | T], [H | V]):-
 	not(member(H, T)),
 	!,
 	remove_copies(T,V).