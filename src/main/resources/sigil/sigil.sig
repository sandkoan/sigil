let == x y =
	__eq x y

let + x y =
	__add x y

let - x y =
	__add x __neg y

let neg x =
	__neg x

let * x y =
	__mul x y

let / x y =
	__div x y

let % x y =
	__rem x y

let ! x =
	if == true x
		false
	if == false x
		true
	x

let or x y =
	if == true x
		true
	if == true y
		true
	false

let and x y =
	if == true x
		if == true y
			true
		false
	false

let head x =
	__head x

let tail x =
	__tail x

let fuse x y =
	__fuse x y

let pair x y =
	__pair x y

let litr x =
	__litr x

let str x =
	__str x

let words x =
	__words x

let input x =
	__input x

let print x =
	__print x

let # x y =
	head pair y x

let @ x y =
	# "Evaluate to only the first argument"
	# y x

let wrap x =
	# "Wrap a value in a list"
	tail pair null x

let empty =
	# "Produce the empty list"
	tail wrap null

let debug_enabled =
	# "Whether debugging output should occur"
	false

let debug info x =
	# "Describing the value while returning it."
	if debug_enabled
		# print + "DEBUG [" + str info + "]: " str x
		x
	x

let assert info x =
	# "Output a warning if the value is not true"
	if debug_enabled
		if == true x
			true
		# print + "ASSERTION FAILED [" + str info + "]: " str x
		false
	true

let assert_eq x y =
	# "Output a warning if the values are not equal"
	if debug_enabled
		if == x y
			true
		# print + "ASSERTION FAILED [" + str x + " != " + str y "]"
		false
	true

let is_atom x =
	# "Determine whether a value is not list-like"
	if == x head x
		true
	false

let is_str x =
	# "Determine whether a value is a string"
	== x str x

let is_list x =
	# "Determine whether a value is a list"
	if is_str x
		false
	if is_atom x
		false
	true

let is_bool x =
	# "Determine whether a value is a bool"
	if == true x
		true
	if == false x
		true
	false

let is_null x =
	# "Determine whether a value is null"
	if == null x
		true
	false

let is_num x =
	# "Determine whether a value is a number"
	if == 0 * x 0
		true
	false

let len l =
	# "Find the length of a list or string"
	if is_atom l
		1
	if == empty l
		0
	+ 1 len tail l

let skip n l =
	# "Find the nth tail of a list"
	if is_atom l
		l
	if == 0 n
		l
	skip - n 1 tail l

let nth n l =
	# "Find the nth value of a list or string"
	if is_atom l
		l
	if == 0 n
		head l
	nth - n 1 tail l

let in x l =
	# "Determine whether a value exists within a list"
	if ! is_list l
		false
	if == empty l
		false
	if == x head l
		true
	in x tail l

let split x l =
	# "Split a list into two sublists at the given index"
	if == empty l
		empty
	if == x 0
		pair empty l
	pair fuse head l head split - x 1 tail l
		 head tail split - x 1 tail l
