#!/usr/bin/env python3
"""
Google Cloud Vision API Test Script

This script demonstrates how to call the Google Cloud Vision API for handwriting recognition
using a base64-encoded image.

Usage:
    python test_vision_api.py <api_key> <image_file_path>

Example:
    python test_vision_api.py YOUR_API_KEY handwriting_sample.jpg
"""

import base64
import json
import sys
import os
import requests

def image_to_base64(image_path):
    """
    Convert an image file to a base64 encoded string
    
    Args:
        image_path (str): Path to the image file
        
    Returns:
        str: Base64 encoded string
    """
    try:
        with open(image_path, "rb") as image_file:
            encoded_string = base64.b64encode(image_file.read()).decode('utf-8')
            return encoded_string
    except FileNotFoundError:
        print(f"Error: File '{image_path}' not found.")
        return None
    except Exception as e:
        print(f"Error: {str(e)}")
        return None

def call_vision_api(api_key, base64_image):
    """
    Call the Google Cloud Vision API for handwriting recognition
    
    Args:
        api_key (str): Google Cloud API key
        base64_image (str): Base64 encoded image
        
    Returns:
        dict: API response
    """
    url = f"https://vision.googleapis.com/v1/images:annotate?key={api_key}"
    
    payload = {
        "requests": [
            {
                "image": {
                    "content": base64_image
                },
                "features": [
                    {
                        "type": "DOCUMENT_TEXT_DETECTION"
                    }
                ]
            }
        ]
    }
    
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.post(url, headers=headers, json=payload)
        return response.json()
    except Exception as e:
        print(f"Error calling API: {str(e)}")
        return None

def main():
    if len(sys.argv) < 3:
        print("Usage: python test_vision_api.py <api_key> <image_file_path>")
        sys.exit(1)
        
    api_key = sys.argv[1]
    image_path = sys.argv[2]
    
    if not os.path.exists(image_path):
        print(f"Error: File '{image_path}' not found.")
        sys.exit(1)
        
    print(f"Converting image to base64: {image_path}")
    base64_image = image_to_base64(image_path)
    
    if not base64_image:
        print("Failed to convert image to base64.")
        sys.exit(1)
    
    print("Calling Google Cloud Vision API...")
    response = call_vision_api(api_key, base64_image)
    
    if not response:
        print("Failed to get response from API.")
        sys.exit(1)
    
    # Check for errors
    if "error" in response:
        print("API Error:")
        print(json.dumps(response["error"], indent=2))
        sys.exit(1)
    
    # Extract and print the detected text
    if "responses" in response and len(response["responses"]) > 0:
        if "fullTextAnnotation" in response["responses"][0]:
            text = response["responses"][0]["fullTextAnnotation"]["text"]
            print("\nDetected Text:")
            print("-" * 50)
            print(text)
            print("-" * 50)
        else:
            print("No text detected in the image.")
    else:
        print("Unexpected API response format.")
        print(json.dumps(response, indent=2))
    
    # Save full response to file
    output_file = f"{os.path.splitext(image_path)[0]}_response.json"
    with open(output_file, "w") as f:
        json.dump(response, f, indent=2)
    print(f"\nFull response saved to: {output_file}")

if __name__ == "__main__":
    main()