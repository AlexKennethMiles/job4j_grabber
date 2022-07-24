CREATE TABLE company (
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person (
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company values (1, 'A');
insert into company values (2, 'B');
insert into company values (3, 'C');
insert into company values (4, 'D');
insert into company values (5, 'E');

insert into person values (1, 'Heidi Wallace', 1);
insert into person values (2, 'Mary Freeman', 1);
insert into person values (3, 'Kathleen Henry', 1);
insert into person values (4, 'Melanie Roberts', 1);
insert into person values (5, 'Steven Murphy', 2);
insert into person values (6, 'Tammy Daniels', 2);
insert into person values (7, 'Doris Smith', 2);
insert into person values (8, 'Donald King', 2);
insert into person values (9, 'Robert Jacobs', 2);
insert into person values (10, 'Rose Lyons', 3);
insert into person values (11, 'Sharon Rodriguez', 3);
insert into person values (12, 'Sandra Wilson', 3);
insert into person values (13, 'Melissa Smith', 4);
insert into person values (14, 'Joshua Russell', 4);
insert into person values (15, 'Aaron Wagner', 4);
insert into person values (16, 'Linda Johnson', 5);
insert into person values (17, 'Shirley Owens', 5);
insert into person values (18, 'Carl Bennett', 5);
insert into person values (19, 'James Hudson', 5);
insert into person values (20, 'Edwin Burgess', 5);

select p.name, c.name
from person p
         left join company c
                   on p.company_id = c.id
where p.company_id != 5;

select c.name, count(*) as "Кол-во персонала"
from company c
         left join person p
                   on c.id = p.company_id
group by c.name
having count(*) = (select max(cnt)
                   from (select count(*) as cnt
                         from person p
                         group by p.company_id) as abc
);