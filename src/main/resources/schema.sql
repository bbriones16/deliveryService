drop table if exists test.pricing;

create table test.pricing (
	priority integer not null, 
	rule varchar(50), 
	description varchar(50), 
	cost decimal (6, 2),
	primary key (priority)
);
commit;