create table Languages (
	id int auto_increment primary key,
	name varchar(10)
);
create table Clients (
	id bigint auto_increment primary key,
	name varchar(40) unique not null
);
create table Users (
	id bigint auto_increment primary key,
	ipAddress varchar(40) unique not null
);
create table Enquetes (
    id bigint auto_increment primary key,
    user_id bigint unique references User(id),
    client_id bigint references Client(id),
    description varchar(500) not null,
    total int not null default 0,
    language_id int not null references Language(id),
    created timestamp not null default CURRENT_TIMESTAMP
);
create table Entries (
	id bigint primary key,
    enquete_id bigint not null references Enquete(id),
	number int not null,
    string varchar(50) not null
);

create table Answers (
	id bigint primary key,
	user_id bigint not null references User(id),
	enquete_id bigint not null references Enquete(id),
	entry int not null,
	created timestamp not null default CURRENT_TIMESTAMP,
);

create table DealtEnquete (
	enquete_id bigint primary key references Enquete(id)
);
