package com.example.androidcamera.models

import com.google.gson.annotations.SerializedName

/**
 * Request model for Google Cloud Vision API
 */
data class VisionRequest(
    val requests: List<AnnotateImageRequest>
)

data class AnnotateImageRequest(
    val image: Image,
    val features: List<Feature>
)

data class Image(
    val content: String // Base64-encoded image data
)

data class Feature(
    val type: String, // e.g., "DOCUMENT_TEXT_DETECTION"
    val maxResults: Int? = null
)

/**
 * Response model for Google Cloud Vision API
 */
data class VisionResponse(
    val responses: List<AnnotateImageResponse>
)

data class AnnotateImageResponse(
    val textAnnotations: List<TextAnnotation>? = null,
    val fullTextAnnotation: FullTextAnnotation? = null,
    val error: Error? = null
)

data class TextAnnotation(
    val locale: String? = null,
    val description: String? = null,
    val boundingPoly: BoundingPoly? = null
)

data class BoundingPoly(
    val vertices: List<Vertex>
)

data class Vertex(
    val x: Int,
    val y: Int
)

data class FullTextAnnotation(
    val pages: List<Page>? = null,
    val text: String? = null
)

data class Page(
    val property: TextProperty? = null,
    val width: Int? = null,
    val height: Int? = null,
    val blocks: List<Block>? = null
)

data class TextProperty(
    val detectedLanguages: List<DetectedLanguage>? = null
)

data class DetectedLanguage(
    @SerializedName("languageCode")
    val languageCode: String? = null,
    val confidence: Float? = null
)

data class Block(
    val boundingBox: BoundingPoly? = null,
    val paragraphs: List<Paragraph>? = null,
    val blockType: String? = null
)

data class Paragraph(
    val boundingBox: BoundingPoly? = null,
    val words: List<Word>? = null
)

data class Word(
    val boundingBox: BoundingPoly? = null,
    val symbols: List<Symbol>? = null
)

data class Symbol(
    val boundingBox: BoundingPoly? = null,
    val text: String? = null
)

data class Error(
    val code: Int? = null,
    val message: String? = null
)