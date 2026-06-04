# Weather App Backend

This Weather App not only shows forecasts for different cities, it also provides itinerary reccomendations to users for a day trip in their area based on hourly weather which includes pricing for attractions, tips and details, as well as the ideal visitng time. On the attractions tab, nearby attractions appear appear on a map where users can select and view the details of attractions, and select, reorder, or delete them to create their own route. Finally, users may share the full Claude generated or self managed itinerary through Gmail/Messenger or export their itinerary as a PDF.

## Tech Stack
### Backend
- A Java Springboot Backend that stores and manages the city and user data, as well as interacting with third party APIs such as Anthropic API for AI Generated itineraries, Geolocation to determin the user's city, and OpenWeather API to access current weather and forecasts for different cities.

### Frontend (https://github.com/joshuahsin/weather-app-frontend)
- A React frontend complete with a login and registration flow, including options to reset password. User's can view their location's weather on the Home page, search for the forecasts of other locations through the search bar, and save and edit thier saved cities through the "Saved Cities" tab. They can also view nearby attractions on the Attractions tab, and generate a trip itinerary through the itinerary tab. Finally, they have the option to reset their password from the App Bar and logout.

## 🚀 Features

- **Weather API Integration**: Secure access to OpenWeather API with data validation
- **AI-Powered Recommendations**: Google Gemini AI integration for travel itineraries
- **RESTful API Design**: Clean, documented endpoints with proper error handling
- **Security**: CORS protection, input validation, and API key management
- **Scalability**: Batch processing and efficient error handling

## 🌐 API Endpoints

### Users API (`weatherCities`) - Authentication and Registration
- `POST /weatherCities/register`	- Initiate registration — sends 6-digit code to email (valid 15 min)
- `POST	/weatherCities/verifyEmail?email={email}&code={code}` - Verify code and create account
- `POST	/weatherCities/login` - Authenticate — returns true/false
- `POST	/weatherCities/forgotPassword?email={email}` - Send 12-character temporary password to email
- `PATCH /weatherCities/changePassword/{id}`	Change password

## Cities API (`weatherCities`)
- `GET /weatherCities/cities`	- Get all cities
- `GET /weatherCities/city/{id}` -	Get city by ID
- `GET /weatherCities/cityExists?city={city}&state={state}` - Check if a city/state exists in the catalog
- `POST	/weatherCities/city` - Add a city
- `POST	/weatherCities/cities` - Bulk add cities
- `PUT /weatherCities/city/{id}` - Update a city
- `DELETE	/weatherCities/city/{id}`	- Delete city by ID
- `DELETE	/weatherCities/cityByCityState?city={city}&state={state}` -	Delete by city and state

### Saved Cities API (`weatherCities`)
- `GET /weatherCities/savedCities/{userId}` - Get all saved cities for a user
- `POST	/weatherCities/savedCity?userId={userId}&cityId={cityId}` -	Save a catalog city for a user
- `DELETE	/weatherCities/savedCity/{id}` - Remove saved city by record ID
- `DELETE	/weatherCities/savedCityByUserAndCity?userId={userId}&cityId={cityId}` - Remove by user + city pair

### Weather API (`/weatherCities/weather`)
- `GET /weatherCities/weather/health` - Server status and configuration
- `GET /weatherCities/weather/current?lat={lat}&lon={lon}` - Current weather by coordinates
- `GET /weatherCities/weather/current?currentByCityState?city={city}&state={state}&country={country}` - Current weather by city
- `GET /weatherCities/weather/forecast?lat={lat}&lon={lon}`	- 5-day forecast by coordinates
- `GET /weatherCities/weather/forecastByCityState?city={city}&state={state}&country={country}` - 5-day forecast by city

### Geolocation API (`/weatherCities/geolocation`)
- `GET /weatherCities/geolocation/locate?lat={lat}&lon={lon}` - Reverse geocode coordinates to city/state/country

### AI API (`/weatherCities/ai`)
- `POST /weatherCities/ai/itinerary` - AI-powered travel itinerary generation
- `POST /weatherCities/ai/recommendations` - Weather-appropriate activity recommendations

## 📡 API Usage Examples

### Login
```bash
curl -X POST "http://localhost:8080/weatherCities/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "password": "secret123"}'
```

### Get all cities
```bash
curl "http://localhost:8080/weatherCities/cities"
```

### Add a city
```bash
curl -X POST "http://localhost:8080/weatherCities/city" \
  -H "Content-Type: application/json" \
  -d '{"city": "Austin", "state": "TX", "country": "USA"}'
```

### Check Existence of City in DB
```bash
curl "http://localhost:8080/weatherCities/cityExists?city=Austin&state=TX"
```

### Get saved cities for user 1
```bash
curl "http://localhost:8080/weatherCities/savedCities/1"
```

### Save City ID 42 for user 1
```bash
curl -X POST "http://localhost:8080/weatherCities/savedCity?userId=1&cityId=42"
```

### Remove Saved City by User + City
```bash
curl -X DELETE "http://localhost:8080/weatherCities/savedCityByUserAndCity?userId=1&cityId=42"
```
  
### Get Current Weather
```bash
curl "http://localhost:3001/api/weather/current?lat=40.7128&lon=-74.0060"
```

### 5-day forecast
```bash
curl "http://localhost:8080/weatherCities/weather/forecast?lat=40.7128&lon=-74.0060"
```

### Generate Travel Itinerary
```bash
curl -X POST "http://localhost:3001/api/ai/itinerary" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Generate a travel itinerary",
    "context": {
      "location": {"lat": 40.7128, "lng": -74.0060},
      "weather": {"temperature": 75, "description": "sunny"},
      "currentTime": "2024-01-15T10:00:00Z"
    }
  }'
```

## 🔧 Configuration
### API Rate Limits
- Weather API: 60 calls/minute (OpenWeather free tier)
- AI API: Based on your Gemini AI quota
- Batch requests: Maximum 10 locations per request

## 🛡️ Security Features

- **Input Validation**: All coordinates and inputs are validated
- **CORS Protection**: Configurable cross-origin resource sharing
- **Helmet**: Security headers for Express
- **Request Logging**: Morgan logging for monitoring
- **Error Handling**: Comprehensive error handling and logging
- **API Key Protection**: API keys stored securely in environment variables

## 📊 Error Handling

The API returns consistent error responses:
```json
{
  "error": "Error type",
  "message": "Human-readable error message",
  "status": 400
}
```

### Common HTTP Status Codes
- `200`: Success
- `400`: Bad Request (invalid input)
- `401`: Unauthorized (invalid API key)
- `404`: Not Found (endpoint doesn't exist)
- `422`: Unprocessable Entity (AI response parsing failed)
- `429`: Too Many Requests (rate limit exceeded)
- `500`: Internal Server Error
- `504`: Gateway Timeout (third-party API timeout)
