create table Book(
    Book_id Serial PRIMARY KEY,
    BName varchar not null,
    author varchar not null,
    publisher varchar not null,
    Copies INTEGER not null,
    TotalCopies integer not null
);

-- insert into book (book_id,BName,author,publisher,copies,TotalCopies) values(4,'random book','random','sunka','5','5');

create table Customer(
    Customer_id serial PRIMARY KEY,
    Cname varchar not null,
    Email varchar not null,
    passwordHash varchar not null
);

create table Issue(
    Issue_id serial PRIMARY KEY,
    Book_id integer not null,
    Customer_id integer not null,
    IssueDate date not null,
    DueDate date not null,
    constraint customerRef
        FOREIGN KEY(Customer_id) REFERENCES Customer(Customer_id) on delete CASCADE,
    constraint BookRef
        FOREIGN KEY(Book_id) REFERENCES Book(Book_id) on DELETE CASCADE
);

-- create table Fine(
--     Fine_id serial primary key,
--     Issue_id integer not null,
--     Amt integer not null,
--     constraint IssueRef
--         FOREIGN KEY(Issue_id) REFERENCES Issue(Issue_id) on DELETE CASCADE
-- );

create table Logs(
    Log_id serial PRIMARY key,
    Book_id integer not null,
    Customer_id integer not null,
    Fine_Amt integer not null check(Fine_Amt >= -1),
    Log_type varchar not null check (Log_type in ('fine','issue','return')),
    Event_Date date,
    constraint customerRefLogs
        FOREIGN KEY(Customer_id) REFERENCES Customer(Customer_id) on delete CASCADE,
    constraint BookRefLogs
        FOREIGN KEY(Book_id) REFERENCES Book(Book_id) on DELETE CASCADE
);

create table rules(
    Max_allowed_books integer not null default 5 check ( Max_allowed_books > 3),
    Max_allowed_books_per_month integer not null default 3 check (Max_allowed_books_per_month > 1)
);

insert into rules values(5,3);

create or replace PROCEDURE issue_book(
                b_id integer,
                c_id integer,
                period integer )
as $$
DECLARE
    totalCount integer;
    monthCount integer;
    copiesAvailable integer;
    mab integer;
    mabm integer;
begin 

    if b_id not in (select book_id from book) or c_id not in (select customer_id from customer) THEN
        raise exception 'Book or customer not found'; 
    end if ;
    if b_id in (select book_id from issue where customer_id = c_id) then
        RAISE EXCEPTION 'User already has the same book';
    end if ;
    select copies into copiesAvailable from book where book_id = b_id;
    if copiesAvailable > 0 THEN
        select count(Book_id) into totalCount from Issue where Customer_id = c_id;
        select count(Book_id) into monthCount from Issue where Customer_id = c_id and abs(date_part('month',IssueDate) - date_part('month',CURRENT_DATE)) <= 1 ;
        select max_allowed_books into mab from rules order by max_allowed_books DESC limit 1;
        select  max_allowed_books_per_month into mabm from rules order by max_allowed_books DESC limit 1;
        if totalCount < mab and monthCount < mabm then 
            insert into logs (book_id,customer_id,event_date,log_type,fine_amt) 
                values (b_id,c_id,CURRENT_DATE,'issue',-1);
            insert into issue (book_id,customer_id,issuedate,duedate)
                values (b_id,c_id,CURRENT_DATE,CURRENT_DATE + period);
            update book set copies = copies -1 where book_id = b_id;
        ELSE
            RAISE EXCEPTION 'User already has too many books';
        end if ;
    ELSE
        RAISE EXCEPTION 'Book not available';
    end if ;

end; $$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION getFine(issueId integer) RETURNS INT AS $$
DECLARE
    lastDate date;
BEGIN
    if issueId is null THEN
        raise exception 'No Issue record Found';
    end if;
    select duedate into lastDate from issue where issue_id = issueId;
    if lastDate < current_date THEN
        return extract(epoch from age(CURRENT_DATE,lastDate))/60*60*24;
    end if;
    return 0;
END;
$$ LANGUAGE plpgsql;


create or replace procedure return_book(
                b_id integer,
                c_id integer )
as $$
declare
    issueId integer;
begin
    select issue_id into issueId from issue where book_id = b_id and customer_id = c_id;
    if issueId is null THEN
        raise exception 'No Issue record Found';
    end if;
    insert into logs (book_id,customer_id,event_date,log_type,fine_amt) 
                values (b_id,c_id,CURRENT_DATE,'return',getFine(issueId));
    delete from issue where book_id = b_id and customer_id = c_id;
    update book set copies = copies +1 where book_id = b_id;

end; $$ language plpgsql; 


create or replace procedure renew_book(
                b_id integer,
                c_id integer,
                period integer )
as $$
begin
    call return_book(b_id,c_id);
    call issue_book(b_id,c_id,period);
end; $$ language plpgsql;



-- select count(Book_id) into totalCount from Issue where Customer_id = 6;

create table admins(
	username varchar not null,
	password varchar not null
);

create table librarian(
	id serial not null primary key,
	name varchar not null,
	password varchar not null,
	email varchar not null,
	address varchar not null,
	city varchar not null,
	contact varchar not null
);