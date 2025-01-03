# Antenka
## REST API written in Java. 

Main goal of this project was to learn and improve:
1) ORM framework - Hibernate,
2) Spring.

It is REST API for a mobile app (AntenkaMobile), which will be created as next step of the project Antenka development.

Antenka is a project where users can:
1) add a volleyball match and find its players,
2) find a voleyball match to play.

Basic concept is an event (abstract `Event`) - at this moment only `Match` and its placeholders for players `Slots`s. Every `Slot` has requirements about a wanted player and a player, who applied on this.
Because of a biderectional relationship between `Match`and `Slot`, it is easy to use `Slot` 's reference to `Match` and get informations about a match.

![obraz](https://github.com/GosiaGie/antenka/assets/52133577/5646416b-66e4-481d-999e-4ebcaf4d963b)


## Registration
Every user needs to registers with email, password, first name, last name and birthday.


```http
POST /auth/register
```

```json
{
  "email" : "jan.kowalski@mail.com",
  "password" : "SuperPassword1!",
  "firstName" : "Jan",
  "lastName" : "Kowalski",
  "birthday" : "2000-01-01"
}
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `email` | `string` | **Required**. Unique for every user. |
| `password` | `string` | **Required**. Min. 8 char., min. 1 special char., min. 1 digit. Password is stored encrypted.|
| `firstName` | `string` | **Required**. Only letters. |
| `lastName` | `string` | **Required**. Only letters. |
| `birthday` | `date` | **Required**. Age over 16 and under 150. |


### Response
Example of a successful registration response:
```json
{
    "email": "jan.kowalski@mail.com",
    "registerInfo": [
        "OK"
    ]
}
```

Example of a unsuccessful registration response:
```json
{
    "email": "jan.kowalski@mail.com",
    "registerInfo": [
        "PASSWORD_DOES_NOT_MEET_REQ",
        "EMAIL_ALREADY_EXISTS",
        "INCORRECT_NAME",
        "AGE_UNDER_16"
    ]
}
```




## Authentication
REST API is stateless, so after a sucessful login the application didn't start any session. Every request except `auth/login` and `auth/registration` is secured and requires from a client a valid JWT.
Registered user can get a token after logging in.

```http
POST /auth/login
```

```json
{
    "email": "jan.kowalski@mail.com",
    "password": "SuperPassword1!"
}
```
### Response
After successful login API returns JWT - `accessToken`. Response with http code 403 means that credentials are incorrect.
Successful login example:
```json
{
    "accessToken": "token_here"
}
```

## Adding a match
User adds a match with:

**1) general informations about a match**

**2) slots**

Number of players wanted = number of slots. Every slot enables to add its requirements about a player. Volleyball match requires players on different positions, hence a user/match organizer can add a match
with slots for: 3 outside hitters, 2 middle blockers, 1 libero, 2 setters, 3 right side hitters (the user/match organizer is 12. player).
Other requirements (age, gender, level) can be the same or different.


```http
POST /addMatch
```

```json
{
    "name": "Warszawski Mecz Charytatywny",
    "dateTime": "2024-04-23T18:00",
    "price":{"regularPrice": 20, "benefitPrice":10},
    "address": {"street": "AdolfaPawińskiego", "number": 2, "zipCode": "02106", "locality": "Warsaw", "description": "Hala"},
    "playersNum": 2,
    "players":
   [{"position": "SETTER", "level": "BEGINNER", "gender": "FEMALE", "ageRange":{"ageMin":20, "ageMax":35}},
    {"position": "LIBERO", "level": "BEGINNER", "gender": "FEMALE", "ageRange":{"ageMin":20, "ageMax":35}}] 
}
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `name` | `string` | **Required**. Match name.|
| `dateTime` | `dateTime` | **Required**. Not in the past, below 3 hours from current time or more than 6 months from now. **WARNING** Your match will be auto-closed after it's date and time.|
| `price` | `number` | **Required**. Price per 1 player. If Benefit system is unavailable, then `regularPrice` argument should be equal `benefitPrice` argument.|
| `address` | all `string` | **Every field required except `flatNumber`** . Every parameter of an address is `string. 'street' without white spaces. `zipcode` can be only digits. `Locality` can be only letters.|
| `playersNum` | `integer` | **Required**. Number of players to find. Must be equal `playersWanted` size and max 24.|
| `playersWanted` | `collection` | **Required**. Collection of `playerWanted`. Max size is 24.|
| `playerWanted` | `playerWanted` | **Required**. Requirements about a player wanted to sign up for a `Match`. `Position`: one of `OUTSIDE_HITTER, MIDDLE_BLOCKER, RIGHT_SIDE_HITTER, SETTER, LIBERO`. `Gender`: one of `MALE, FEMALE`. `Level`: one of `BEGINNER, MEDIUM, ADVANCED`. `AgeRange`: 'ageMin': minimal age of a player, 'ageMax': maximal age of a player.

### Response
Example of a successful adding a `Match`:

```json
{
    "addMatchInfo": [
        "OK"
    ],
    "match": {
        "eventID": 5,
        "slots": [
            {
                "id": 9,
                "event": {
                    "eventID": 5
                },
                "orderNum": 1,
                "playerWanted": {
                    "gender": "FEMALE",
                    "ageRange": {
                        "ageMin": 20,
                        "ageMax": 35
                    },
                    "level": "BEGINNER",
                    "position": "SETTER"
                },
                "playerApplied": null
            },
            {
                "id": 10,
                "event": {
                    "eventID": 5
                },
                "orderNum": 2,
                "playerWanted": {
                    "gender": "FEMALE",
                    "ageRange": {
                        "ageMin": 20,
                        "ageMax": 35
                    },
                    "level": "BEGINNER",
                    "position": "LIBERO"
                },
                "playerApplied": null
            }
        ],
        "name": "Warszawski Mecz Charytatywny",
        "price": {
            "regularPrice": 20,
            "benefitPrice": 10
        },
        "address": {
            "addressID": 5,
            "addressType": "EVENT",
            "street": "AdolfaPawińskiego",
            "number": "2",
            "flatNumber": null,
            "zipCode": "02106",
            "locality": "Warsaw",
            "location": {
                "lat": 52.20978179999999,
                "lng": 20.9800504
            },
            "description": "Hala"
        },
        "freeSlots": 2,
        "playersNum": 2,
        "signingUpEndReason": null,
        "dateTime": "2024-11-23T18:00:00",
        "active": true,
        "signingUp": true
    }
}
```


Example of an unsuccessful adding `Match` with list of errors:
```json
{
    "addMatchInfo": [
        "Check your slots number",
        "Date can't be: past date, date under 3 h from now or date after 6 months from now",
        "Incorrect address",
        "Incorrect price. Price can't be under 0.0 and regular price can't be higher than benefit price",
        "Incorrect age: under 16, over 150 or min>max"
    ],
    "match": null
}
```


## Adding Player Profile
`PlayerProfile` is requirement to find matches and them slots.
Age in `PlayerProfile` is calculated based on user's birthday.

```http
POST /addPlayerProfile
```

```json
{
    "positions": ["RIGHT_SIDE_HITTER", "SETTER"],
    "level": "MEDIUM",
    "gender": "FEMALE",
    "benefitCardNumber": "12345"
}
```


| Parameter | Type | Description |
| :--- | :--- | :--- |
| `positions` | `collection` | **Required**. Collection of enum `Position`. At least one element required. Duplicates will be ignored. One or more of 'OUTSIDE_HITTER, MIDDLE_BLOCKER, RIGHT_SIDE_HITTER, SETTER, LIBERO'|
| `level` | `string` | **Required**. One of 'BEGINNER, MEDIUM, ADVANCED'|
| `gender` | `string` | **Required**.  One of `MALE, FEMALE` |
| `benefitCardNumber` | `string` | Not required |


### Response
Example of a successful adding player's profile:
```json
{
    "info": "OK",
    "playerProfile": {
        "playerProfileID": 2,
        "positions": [
            "SETTER",
            "RIGHT_SIDE_HITTER"
        ],
        "level": "MEDIUM",
        "gender": "FEMALE",
        "benefitCardNumber": "12345",
        "apps": [],
        "age": 31,
        "activeBenefit": true,
        "signedUpForActiveEvent": false
    }
}
```


## Finding a match
Results are based on a user's player profile. Results are Matches, where user meets requirements (and all these matches' slots - needs to think it out). At this moment, client sends only a maximal user's price for a `Match`. If a user has an active Benefit card, then Benefit prices are checked. If player doesn't have active benefit card, then only regular price are checked.

```http
POST /findMatch
```

```json
"40"
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `maxPrice` | `string` | **Required**. Maximal price for Match. Format with '.'- example: "10.10"

### Response

```json
{
    "findMatchInfo": "OK",
    "matches": [
        {
            "eventID": 186,
            "name": "Warszawski Mecz Charytatywny",
            "dateTime": "2024-04-23T18:00:00",
            "price": {
                "regularPrice": 20,
                "benefitPrice": 10
            },
            "address": {
                "addressID": 188,
                "addressType": "EVENT",
                "street": "AdolfaPawińskiego",
                "number": "2",
                "flatNumber": null,
                "zipCode": "02106",
                "locality": "Warsaw",
                "location": {
                    "lat": 52.2097818,
                    "lng": 20.9800504
                },
                "description": "Hala"
            },
            "closeReason": null,
            "playersNum": 2,
            "freeSlots": 2,
            "open": true,
            "slots": [
                {
                    "id": 114,
                    "match": {
                        "eventID": 186,
                        "name": "Warszawski Mecz Charytatywny",
                        "dateTime": "2024-04-23T18:00:00",
                        "price": {
                            "regularPrice": 20,
                            "benefitPrice": 10
                        },
                        "address": {
                            "addressID": 188,
                            "addressType": "EVENT",
                            "street": "AdolfaPawińskiego",
                            "number": "2",
                            "flatNumber": null,
                            "zipCode": "02106",
                            "locality": "Warsaw",
                            "location": {
                                "lat": 52.2097818,
                                "lng": 20.9800504
                            },
                            "description": "Hala"
                        }
                    },
                    "orderNum": 2,
                    "playerWanted": {
                        "gender": "FEMALE",
                        "ageRange": {
                            "ageMin": 20,
                            "ageMax": 35
                        },
                        "level": "BEGINNER",
                        "position": "LIBERO"
                    },
                    "playerApplied": null
                },
                {
                    "id": 113,
                    "match": {
                        "eventID": 186,
                        "name": "Warszawski Mecz Charytatywny",
                        "dateTime": "2024-04-23T18:00:00",
                        "price": {
                            "regularPrice": 20,
                            "benefitPrice": 10
                        },
                        "address": {
                            "addressID": 188,
                            "addressType": "EVENT",
                            "street": "AdolfaPawińskiego",
                            "number": "2",
                            "flatNumber": null,
                            "zipCode": "02106",
                            "locality": "Warsaw",
                            "location": {
                                "lat": 52.2097818,
                                "lng": 20.9800504
                            },
                            "description": "Hala"
                        }
                    },
                    "orderNum": 1,
                    "playerWanted": {
                        "gender": "FEMALE",
                        "ageRange": {
                            "ageMin": 20,
                            "ageMax": 35
                        },
                        "level": "BEGINNER",
                        "position": "SETTER"
                    },
                    "playerApplied": null
                }
            ]
        }
    ]
}
```

## Signing Up

```http
POST /signUp
```

```json
{
    "eventID": "186",
    "slotNum":2

}
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `eventID` | `string` | **Required**|
| `slotNum` | `number` | **Required**. Slot's order number in a particular `Match`.|

### Response
```json
{
    "info": "OK",
    "slot": {
        "id": 114,
        "match": {
            "eventID": 186,
            "name": "Warszawski Mecz Charytatywny",
            "dateTime": "2024-04-23T18:00:00",
            "price": {
                "regularPrice": 20,
                "benefitPrice": 10
            },
            "address": {
                "addressID": 188,
                "addressType": "EVENT",
                "street": "AdolfaPawińskiego",
                "number": "2",
                "flatNumber": null,
                "zipCode": "02106",
                "locality": "Warsaw",
                "location": {
                    "lat": 52.2097818,
                    "lng": 20.9800504
                },
                "description": "Hala"
            }
        },
        "orderNum": 2,
        "playerWanted": {
            "gender": "FEMALE",
            "ageRange": {
                "ageMin": 20,
                "ageMax": 35
            },
            "level": "MEDIUM",
            "position": "RIGHT_SIDE_HITTER"
        },
        "playerApplied": {
            "id": 14
        }
    }
}
```

Example of an unsuccessful signing up for a match

```json
{
    "info": "YOU_DO_NOT_MEET_EVENT_REQ",
    "slot": null
}
```

