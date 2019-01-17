insert into `tsugumon`.`Clients` (`name`) values ('井本拓伸');
insert into `tsugumon`.`Languages` (`name`) values ('ja');
insert into `tsugumon`.`Enquetes` (`id`, `client_id`, `description`, `language_id`) values ('1','1', '性別は', '1');
insert into `tsugumon`.`Entries` (`enquete_id`, `number`, `string`) values ('1', '1', '男');
insert into `tsugumon`.`Entries` (`enquete_id`, `number`, `string`) values ('1', '2', '女');
insert into `tsugumon`.`Entries` (`enquete_id`, `number`, `string`) values ('1', '3', 'その他');
insert into `tsugumon`.`DealtEnquete` (`enquete_id`) values ('1');
