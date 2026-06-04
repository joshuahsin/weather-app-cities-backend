# Weather App Backend

This Weather App not only shows forecasts for different cities, it also provides itinerary reccomendations to users for a day trip in their area based on hourly weather which includes pricing for attractions, tips and details, as well as the ideal visitng time. On the attractions tab, nearby attractions appear appear on a map where users can select and view the details of attractions, and select, reorder, or delete them to create their own route. Finally, users may share the full Claude generated or self managed itinerary through Gmail/Messenger or export their itinerary as a PDF.

## Tech Stack
### Backend
- A Java Springboot Backend that stores and manages the city and user data, as well as interacting with third party APIs such as Anthropic API for AI Generated itineraries, Geolocation to determin the user's city, and OpenWeather API to access current weather and forecasts for different cities.

### Frontend
- A React frontend complete with a login and registration flow, including options to reset password. User's can view their location's weather on the Home page, search for the forecasts of other locations through the search bar, and save and edit thier saved cities through the "Saved Cities" tab. They can also view nearby attractions on the Attractions tab, and generate a trip itinerary through the itinerary tab. Finally, they have the option to reset their password from the App Bar and logout.

## 🚀 Features

- **Weather API Integration**: Secure access to OpenWeather API with data validation
- **AI-Powered Recommendations**: Google Gemini AI integration for travel itineraries
- **RESTful API Design**: Clean, documented endpoints with proper error handling
- **Security**: CORS protection, input validation, and API key management
- **Scalability**: Batch processing and efficient error handling

## 🌐 API Endpoints

### Health Check
- `GET /health` - Server status and configuration

### Weather API (`/api/weather`)
- `GET /api/weather/current?lat={lat}&lon={lon}` - Current weather for location
- `GET /api/weather/forecast?lat={lat}&lon={lon}` - 5-day weather forecast
- `POST /api/weather/batch` - Weather data for multiple locations

### AI API (`/api/ai`)
- `POST /api/ai/generate` - General AI content generation
- `POST /api/ai/itinerary` - AI-powered travel itinerary generation
- `POST /api/ai/recommendations` - Weather-appropriate activity recommendations
- `GET /api/ai/health` - AI service status

## 📡 API Usage Examples

### Get Current Weather
```bash
curl "http://localhost:3001/api/weather/current?lat=40.7128&lon=-74.0060"
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

### Batch Weather Request
```bash
curl -X POST "http://localhost:3001/api/weather/batch" \
  -H "Content-Type: application/json" \
  -d '{
    "locations": [
      {"lat": 40.7128, "lng": -74.0060, "name": "New York"},
      {"lat": 34.0522, "lng": -118.2437, "name": "Los Angeles"}
    ]
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
