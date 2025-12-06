Airbnb API Documentation
Hotel
Create Hotel
Method: POST
Path: /api/v1/admin/hotels
Request Body:
{
  "name": "string",
  "city": "string",
  "photos": ["string"],
  "amenities": ["string"],
  "contactInfo": {
    "location": "string",
    "phoneNumber": "string",
    "email": "string",
    "address": "string"
  },
  "active": true
}

Response: Hotel details

Update Hotel
Method: PUT
Path: /api/v1/admin/hotels/{hotelId}
Request Body: Same as Create Hotel
Response: Updated hotel details

Get Hotel by ID
Method: GET
Path: /api/v1/admin/hotels/{hotelId}
Response:
{
  "name": "string",
  "city": "string",
  "photos": ["string"],
  "amenities": ["string"],
  "contactInfo": {
    "location": "string",
    "phoneNumber": "string",
    "email": "string",
    "address": "string"
  },
  "active": true
}


Activate Hotel
Method: PUT
Path: /api/v1/admin/hotels/{hotelId}/activate
Response: Hotel activation status

Delete Hotel
Method: DELETE
Path: /api/v1/admin/hotels/{hotelId}
Response: Deletion status

Room
Create Room
Method: POST
Path: /api/v1/admin/hotels/{hotelId}/rooms
Request Body:
{
  "type": "string",
  "basePrice": 100.0,
  "photos": ["string"],
  "amenities": ["string"],
  "totalCount": 10,
  "capacity": 2
}

Response: Room details

Auth
Signup
Method: POST
Path: /api/v1/auth/signup
Request Body:
{
  "name": "string",
  "email": "string",
  "password": "string"
}

Response: User details

Login
Method: POST
Path: /api/v1/auth/login
Request Body:
{
  "email": "string",
  "password": "string"
}

Response: JWT Token

Logout
Method: POST
Path: /api/v1/auth/logout
Response: Logout status

Bookings
Search Hotels
Method: GET
Path: /api/v1/hotels/search
Query Params:
city=string
startDate=yyyy-MM-dd
endDate=yyyy-MM-dd
page=integer
size=integer

Response: List of hotels

Get Hotel Info
Method: GET
Path: /api/v1/hotels/{hotelId}/info
Response: Hotel details

Initialize Booking
Method: POST
Path: /api/v1/bookings/init
Request Body:
{
  "hotelId": "string",
  "roomId": "string",
  "checkInDate": "yyyy-MM-dd",
  "checkOutDate": "yyyy-MM-dd",
  "roomCount": 1
}

Response: Booking details

Guests
Add Guests to Booking
Method: POST
Path: /api/v1/bookings/{bookingId}/guests
Request Body (Array of Guests):
[
  {
    "name": "Alice Johnson",
    "gender": "FEMALE",
    "age": 28
  },
  {
    "name": "Robert Smith",
    "gender": "MALE",
    "age": 32
  }
]

Response: Updated Booking with Guests
Notes:

gender must be one of: MALE, FEMALE, OTHER.
age must be an integer.
If you include user reference:

{
  "user": { "username": "john_doe" },
  "name": "Alice Johnson",
  "gender": "FEMALE",
  "age": 28
}
