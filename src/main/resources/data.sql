insert into test.pricing (priority, rule, description, cost) values ('1', 'Reject', 'Weight exceeds 50kg', '0');
insert into test.pricing (priority, rule, description, cost) values ('2', 'Heavy Parcel', 'Weight exceeds 10kg', '20');
insert into test.pricing (priority, rule, description, cost) values ('3', 'Small Parcel', 'Volume is less than 1500 cm 3', '0.03');
insert into test.pricing (priority, rule, description, cost) values ('4', 'Medium Parcel', 'Volume is less than 2500 cm 3', '0.04');
insert into test.pricing (priority, rule, description, cost) values ('5', 'Large Parcel', '', '0.05');
commit;