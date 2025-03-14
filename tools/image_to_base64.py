#!/usr/bin/env python3
"""
Image to Base64 Converter

This script converts an image file to a base64 string that can be used in API requests.
Useful for testing the Google Cloud Vision API with Postman.

Usage:
    python image_to_base64.py <image_file_path>

Example:
    python image_to_base64.py handwriting_sample.jpg
"""

import base64
import sys
import os

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

def main():
    if len(sys.argv) < 2:
        print("Usage: python image_to_base64.py <image_file_path>")
        sys.exit(1)
        
    image_path = sys.argv[1]
    
    if not os.path.exists(image_path):
        print(f"Error: File '{image_path}' not found.")
        sys.exit(1)
        
    base64_string = image_to_base64(image_path)
    
    if base64_string:
        print("\nBase64 encoded string:")
        print(base64_string)
        
        # Save to file
        output_file = f"{os.path.splitext(image_path)[0]}_base64.txt"
        with open(output_file, "w") as f:
            f.write(base64_string)
        print(f"\nBase64 string saved to: {output_file}")
        
        # Print instructions for Postman
        print("\nInstructions for Postman:")
        print("1. Copy the base64 string from the output file")
        print("2. In Postman, set the environment variable 'base64_image' to this value")
        print("3. Use the request body with {{base64_image}} placeholder")
        print("4. Make sure to set your Google Cloud API key in the 'api_key' environment variable")

if __name__ == "__main__":
    main()