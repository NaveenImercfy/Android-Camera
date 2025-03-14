# Android Camera with Google Cloud Vision API

This repository contains an Android application that demonstrates how to:
1. Capture images using the Android camera
2. Convert images to base64 format
3. Send images to Google Cloud Vision API for handwriting recognition
4. Process and display the recognition results

## Features

- Camera integration using CameraX library
- Runtime permissions handling
- Image capture and processing
- Base64 encoding/decoding utilities
- Google Cloud Vision API integration
- Handwriting recognition and text extraction

## Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- Android SDK 21 or higher
- Google Cloud Platform account with Vision API enabled
- API key for Google Cloud Vision API

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/NaveenImercfy/Android-Camera.git
cd Android-Camera
```

### 2. Set up Google Cloud Vision API
1. Create a project in the [Google Cloud Console](https://console.cloud.google.com/)
2. Enable the Vision API for your project
3. Create an API key
4. Add the API key to your `local.properties` file:
   ```
   VISION_API_KEY=your_api_key_here
   ```

### 3. Build and run the application
Open the project in Android Studio, build, and run on a device or emulator.

## Making API Calls to Google Cloud Vision API

### API Endpoint
```
POST https://vision.googleapis.com/v1/images:annotate
```

### Request Headers
```
Content-Type: application/json
```

### Request Parameters
Add your API key as a query parameter:
```
?key=YOUR_API_KEY
```

### Request Body
```json
{
  "requests": [
    {
      "image": {
        "content": "BASE64_ENCODED_IMAGE"
      },
      "features": [
        {
          "type": "DOCUMENT_TEXT_DETECTION"
        }
      ]
    }
  ]
}
```

### Sample Postman Request

1. Set up a POST request to `https://vision.googleapis.com/v1/images:annotate?key={{api_key}}`
2. Add header: `Content-Type: application/json`
3. Add the request body as shown above, replacing `BASE64_ENCODED_IMAGE` with your actual base64-encoded image
4. Send the request

### Response Format
The API will return a JSON response with detected text and confidence scores:

```json
{
  "responses": [
    {
      "textAnnotations": [
        {
          "locale": "en",
          "description": "The full text extracted from the image",
          "boundingPoly": {
            "vertices": [
              { "x": 0, "y": 0 },
              { "x": 100, "y": 0 },
              { "x": 100, "y": 100 },
              { "x": 0, "y": 100 }
            ]
          }
        },
        // Additional annotations for each word/character
      ],
      "fullTextAnnotation": {
        "pages": [
          // Detailed text information
        ],
        "text": "The full text extracted from the image"
      }
    }
  ]
}
```

## Project Structure

- `app/src/main/java/com/example/androidcamera/`
  - `MainActivity.kt`: Entry point of the application
  - `CameraActivity.kt`: Camera functionality implementation
  - `ApiService.kt`: Retrofit service for API calls
  - `utils/`: Utility classes for image processing and base64 conversion
  - `models/`: Data classes for API requests and responses

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Google Cloud Vision API Documentation](https://cloud.google.com/vision/docs/handwriting)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [Retrofit Documentation](https://square.github.io/retrofit/)