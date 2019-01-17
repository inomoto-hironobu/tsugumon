select
	*
from
	Enquetes
where
	description like /*@infix(keyword)*/'test'
	limit /*limit*/0 offset /*offset*/0