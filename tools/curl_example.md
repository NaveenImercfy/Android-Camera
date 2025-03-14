# Google Cloud Vision API - Curl Example

This document provides a curl command example for testing the Google Cloud Vision API for handwriting recognition.

## Prerequisites

1. A Google Cloud Platform account with the Vision API enabled
2. An API key for authentication
3. A base64-encoded image containing handwritten text

## Curl Command

```bash
curl -X POST \
  "https://vision.googleapis.com/v1/images:annotate?key=YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "requests": [
      {
        "image": {
          "content": "YOUR_BASE64_ENCODED_IMAGE"
        },
        "features": [
          {
            "type": "DOCUMENT_TEXT_DETECTION"
          }
        ]
      }
    ]
  }'
```

Replace:
- `YOUR_API_KEY` with your actual Google Cloud API key
- `YOUR_BASE64_ENCODED_IMAGE` with your base64-encoded image

## Important Notes

1. Make sure your API key is correctly formatted and has the Vision API enabled
2. The API key should be passed as a query parameter (`?key=YOUR_API_KEY`) and not in the JSON body
3. The base64-encoded image should not include any line breaks or spaces
4. For large images, you may need to compress them before encoding to base64

## Example with Python

You can use the provided Python script to generate the base64-encoded image:

```bash
python tools/image_to_base64.py path/to/your/image.jpg
```

Then use the output in your curl command.

## Troubleshooting

If you receive an error like:

```json
{
    "error": {
        "code": 400,
        "message": "Invalid JSON payload received. Unknown name \"?key\": Cannot bind query parameter. Field '?key' could not be found in request message.",
        "status": "INVALID_ARGUMENT",
        "details": [
            {
                "@type": "type.googleapis.com/google.rpc.BadRequest",
                "fieldViolations": [
                    {
                        "description": "Invalid JSON payload received. Unknown name \"?key\": Cannot bind query parameter. Field '?key' could not be found in request message."
                    }
                ]
            }
        ]
    }
}
```

This typically means:
1. The API key is incorrectly formatted in the request URL
2. You're trying to include the API key in the JSON body instead of as a query parameter

Make sure the API key is passed as a query parameter in the URL, not in the JSON body.