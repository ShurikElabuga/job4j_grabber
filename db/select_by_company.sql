CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company(id, name)
values(1, 'company1'), (2, 'company2'),
(3, 'company3'), (4, 'company4'),
(5, 'company5'), (6, 'company6');

insert into person(id, name, company_id)
values(1, 'person1', 1), (2, 'person2', 1), (3, 'person3', 2), (4, 'person4', 3), (5, 'person5', 2),
(6, 'person6', 5), (7, 'person7', 5), (8, 'person8', 1), (9, 'person9', 5), (10, 'person10', 6),
(11, 'person11', 5), (12, 'person12', 2), (13, 'person13', 2), (14, 'person14', 4), (15, 'person15', 2),
(16, 'person16', 5), (17, 'person17', 3), (18, 'person18', 4), (19, 'person19', 6), (20, 'person20', 6);

-- task1

select p.name as "person", c.name as "company" from person p
left join company c on p.company_id = c.id
where p.company_id != 5;

--task2

select c.name "company", count(company_id) "max_person"
from company c right join person p on c.id = p.company_id
group by c.name, company_id having count(p.id) = (select count(company_id)
                                      from person
									  group by company_id
									  order by 1 desc limit 1);