insert into Languages values(1,'ja-JP');

insert into Users (id,ipAddress) values(1,'127.0.0.1');
insert into Users (id,ipAddress) values(2,'192.168.10.2');
insert into Users (id,ipAddress) values(3,'192.168.10.3');
insert into Users (id,ipAddress) values(4,'192.168.10.4');
insert into Users (id,ipAddress) values(5,'192.168.10.5');
insert into Users (id,ipAddress) values(6,'192.168.10.6');
insert into Users (id,ipAddress) values(7,'192.168.10.7');
insert into Users (id,ipAddress) values(8,'192.168.10.8');
insert into Users (id,ipAddress) values(9,'192.168.10.9');
insert into Users (id,ipAddress) values(10,'192.168.10.10');

insert into Clients values(1,'test');

insert into Enquetes values(1,1,null,'性別は',4,1,'2010-11-11 11:11:11');
insert into Enquetes values(2,2,null,'年齢は',3,1,'2010-11-12 11:11:11');
insert into Enquetes values(3,3,null,'国籍は',3,1,'2010-11-13 11:11:11');
insert into Enquetes values(4,4,null,'<script>alert("test")</script>',3,1,'2010-11-14 11:11:11');
insert into Enquetes values(5,5,null,'test1',3,1,'2010-11-15 11:11:11');
insert into Enquetes values(6,6,null,'test2',3,1,'2010-11-16 11:11:11');
insert into Enquetes values(7,7,null,'test3',2,1,'2010-11-17 11:11:11');
insert into Enquetes values(8,8,null,'test4',2,1,'2010-11-18 11:11:11');
insert into Enquetes values(9,9,null,'test5',2,1,'2010-11-19 11:11:11');
insert into Enquetes values(10,10,null,'test6',0,1,'2010-11-20 11:11:11');
insert into Enquetes values(11,null,1,'test8',0,1,'2010-11-22 11:11:11');

insert into Entries values(1,1,'男');
insert into Entries values(1,2,'女');
insert into Entries values(1,3,'その他');

insert into Entries values(2,1,'20未満');
insert into Entries values(2,2,'20-40');
insert into Entries values(2,3,'40-60');
insert into Entries values(2,4,'60-80');
insert into Entries values(2,5,'80-');

insert into Entries values(3,1,'日本');
insert into Entries values(3,2,'日本以外');

insert into Entries values(4,1,'e1');
insert into Entries values(4,2,'e2');

insert into Entries values(5,1,'e1');
insert into Entries values(6,1,'e1');
insert into Entries values(7,1,'e1');
insert into Entries values(8,1,'e1');
insert into Entries values(9,1,'e1');
insert into Entries values(10,1,'e1');

insert into Answers values(1,1,1,'2011-11-11 11:11:11');
insert into Answers values(1,2,2,'2011-11-11 11:11:11');
insert into Answers values(1,3,3,'2011-11-11 11:11:11');
insert into Answers values(1,4,1,'2011-11-11 11:11:11');
insert into Answers values(1,5,1,'2011-11-11 11:11:11');

insert into Answers values(2,1,1,'2011-11-11 11:11:11');
insert into Answers values(2,2,2,'2011-11-11 11:11:11');
insert into Answers values(2,3,3,'2011-11-11 11:11:11');
insert into Answers values(2,4,1,'2011-11-11 11:11:11');
insert into Answers values(2,5,1,'2011-11-11 11:11:11');
insert into Answers values(2,6,1,'2011-11-11 11:11:11');
insert into Answers values(2,7,1,'2011-11-11 11:11:11');
insert into Answers values(2,8,1,'2011-11-11 11:11:11');
insert into Answers values(2,9,1,'2011-11-11 11:11:11');

insert into Answers values(3,1,1,'2011-11-11 11:11:11');
insert into Answers values(3,2,2,'2011-11-11 11:11:11');
insert into Answers values(3,3,3,'2011-11-11 11:11:11');
insert into Answers values(3,4,1,'2011-11-11 11:11:11');
insert into Answers values(3,5,1,'2011-11-11 11:11:11');
insert into Answers values(3,6,1,'2011-11-11 11:11:11');
insert into Answers values(3,7,1,'2011-11-11 11:11:11');
insert into Answers values(3,8,1,'2011-11-11 11:11:11');
insert into Answers values(3,9,1,'2011-11-11 11:11:11');
insert into Answers values(3,10,1,'2011-11-11 11:11:11');
insert into Answers values(3,11,1,'2011-11-11 11:11:11');

insert into Answers values(4,1,1,'2011-11-11 11:11:11');

insert into DealtEnquete values(11);