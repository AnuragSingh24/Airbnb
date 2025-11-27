**Airbnb API Summary**
**Hotel**
Create hotel
Method: POST
Path: unknown
Input:
{    "name": "string",    "city": "string",    "photos": [        "string"    ],    "amenities": [        "string"    ],    "contactInfo": {        "location": "string",        "phoneNumber": "string",        "email": "string",        "address": "string"    },    "active": "boolean"}
{
    "name": "string",
    "city": "string",
    "photos": [
        "string"
    ],
    "amenities": [
        "string"
    ],
    "contactInfo": {

Output: unknown
Update the hotel
Method: PUT
Path: /api/v1/admin/hotels/2
Input:
Same as Create hotel
Output: unknown
Get hotel by Id
Method: GET
Path: /api/v1/admin/hotels/2
Input: unknown
Output:
{    "name": "string",    "city": "string",    "photos": [    "amenities": [    "contactInfo": {        "location": "string",        "phoneNumber": "string",        "email": "string",        "string"    ],        "string"    ],        "address": "string"    },    "active": "boolean"}
{
    "name": "string",
    "city": "string",
    "photos": [
        "string"
    ],
    "amenities": [
        "string"
    ],
    "contactInfo": {

Activate the hotel
Method: PUT
Path: /api/v1/admin/hotels/4/activate
Input: unknown
Output: unknown
Delete the hotel
Method: DELETE
Path: /api/v1/admin/hotels/3
Input: unknown
Output: unknown
Room
Create Rooms
Method: POST
Path: /api/v1/admin/hotels/1/rooms
Input:
{    "id": "integer",    "type": "string",    "basePrice": "number",    "photos": [    "amenities": [        "string"    ],        "string"    ],    "totalCount": "integer",    "capacity": "integer"}
{
    "id": "integer",
    "type": "string",
    "basePrice": "number",
    "photos": [
        "string"
    ],
    "amenities": [
        "string"
    ],

Output: unknown
**Auth**
Signup
Method: POST
Path: /api/v1/auth/signup
Input:
{    "name": "string",    "email": "string",    "password": "string"}
{
    "name": "string",
    "email": "string",
    "password": "string"
}
Output: unknown
Login
Method: POST
Path: /api/v1/auth/login
Input:
{    "email": "string",    "password": "string"}
{
    "email": "string",
    "password": "string"
}
Output: unknown
Logout
Method: POST
Path: unknown
Input: unknown
Output: unknown
**Bookings**
Search
Method: GET
Path: /api/v1/hotels/search
Input:
{    "city": "string",    "startDate": "string",    "endDate": "string",    "page": "integer",    "size": "integer"}
{
    "city": "string",
    "startDate": "string",
    "endDate": "string",
    "page": "integer",
    "size": "integer"
}
Output: unknown
Get hotel Info
Method: GET
Path: /api/v1/hotels/1/info
Input: unknown
Output: unknown
Initailise booking
Method: POST
Path: /api/v1/bookings/init
Input:
{    "hotelId": "string",    "roomId": "string",    "checkInDate": "string",    "checkOutDate": "string",    "roomCount": "string"}
{
    "hotelId": "string",
    "roomId": "string",
    "checkInDate": "string",
    "checkOutDate": "string",
    "roomCount": "string"
}
Output: unknown
