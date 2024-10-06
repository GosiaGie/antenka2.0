
    create table addresses (
       addressid bigint not null auto_increment,
        address_type varchar(255),
        description varchar(255),
        flat_number varchar(255),
        locality varchar(255),
        latitude float(53),
        longitude float(53),
        number varchar(255),
        street varchar(255),
        zip_code varchar(255),
        primary key (addressid)
    ) engine=InnoDB;

    create table events (
       eventid bigint not null auto_increment,
        close_reason varchar(255),
        date_time datetime(6),
        is_open bit,
        name varchar(255),
        benefit_price decimal(38,2),
        regular_price decimal(38,2),
        addressid bigint,
        organizerid bigint,
        primary key (eventid)
    ) engine=InnoDB;

    create table match_slots (
       slotid bigint not null auto_increment,
        order_num integer,
        age_max integer,
        age_min integer,
        gender varchar(255),
        level varchar(255),
        position varchar(255),
        eventid bigint,
        player_appliedid bigint,
        primary key (slotid)
    ) engine=InnoDB;

    create table matches (
       free_slots integer,
        players_num integer,
        eventid bigint not null,
        primary key (eventid)
    ) engine=InnoDB;

    create table player_positions (
       playerid bigint not null,
        position varchar(255)
    ) engine=InnoDB;

    create table player_profiles (
       playerid bigint not null auto_increment,
        benefit_card_number varchar(255),
        gender varchar(255),
        level varchar(255),
        userid bigint,
        primary key (playerid)
    ) engine=InnoDB;

    create table users (
       userid bigint not null auto_increment,
        birthday date,
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        password varchar(255),
        role varchar(255),
        primary key (userid)
    ) engine=InnoDB;

    alter table events 
       add constraint FKkvnjhwn6xo98nwy8r3ch1bjq8 
       foreign key (addressid) 
       references addresses (addressid);

    alter table events 
       add constraint FKclonl1mdi0sa22px4w38s6v92 
       foreign key (organizerid) 
       references users (userid);

    alter table match_slots 
       add constraint FK8nee57t8v6f7729kyecexefg4 
       foreign key (eventid) 
       references matches (eventid);

    alter table match_slots 
       add constraint FK8nncdl47ebl8q4m5xqs7d9yo1 
       foreign key (player_appliedid) 
       references player_profiles (playerid);

    alter table matches 
       add constraint FKefjv7mujdn20hwg3h7u6326ey 
       foreign key (eventid) 
       references events (eventid) 
       on delete cascade;

    alter table player_positions 
       add constraint FKrln8fc67ibh41ppg0xru4it5s 
       foreign key (playerid) 
       references player_profiles (playerid);

    alter table player_profiles 
       add constraint FKo1p8rlewid1j5sy0umcw8cx68 
       foreign key (userid) 
       references users (userid);
