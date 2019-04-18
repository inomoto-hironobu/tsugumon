create table Languages (
	id int primary key,
	name varchar(10)
);
create table Users (
	id bigint primary key,
	ipAddress varchar(40) unique not null
);
create table Enquetes (
    id bigint primary key,
    user_id bigint unique references Users(id),
    content varchar(500) not null,
    total int not null default 0,
    language_id int not null references Languages(id),
    created timestamp not null default CURRENT_TIMESTAMP
);
create table Entries (
	id bigint primary key,
    enquete_id bigint not null references Enquetes(id),
	number int not null,
    content varchar(50) not null,
    unique(enquete_id, number)
);

create table Answers (
	id bigint primary key,
	user_id bigint not null references Users(id),
	entry_id bigint not null references Entries(id),
	created timestamp not null default CURRENT_TIMESTAMP
);
