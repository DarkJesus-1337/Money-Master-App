package com.pixelpioneer.moneymaster.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents the response from the OCR Space API.
 *
 * @property parsedResults List of parsed results from the image.
 * @property ocrExitCode Exit code of the OCR process.
 * @property isErroredOnProcessing Indicates if an error occurred during processing.
 * @property errorMessage Error message(s), if any.
 * @property errorDetails Additional error details, if any.
 */
data class OcrSpaceResponse(
    @SerializedName("ParsedResults")
    val parsedResults: List<ParsedResult>?,

    @SerializedName("OCRExitCode")
    val ocrExitCode: Int,

    @SerializedName("IsErroredOnProcessing")
    val isErroredOnProcessing: Boolean,

    @SerializedName("ErrorMessage")
    val errorMessage: Any?,

    @SerializedName("ErrorDetails")
    val errorDetails: Any?
)

/**
 * Represents a single parsed result from the OCR Space API.
 *
 * @property textOverlay Overlay information for detected text.
 * @property textOrientation Orientation of the detected text.
 * @property fileParseExitCode Exit code for file parsing.
 * @property parsedText The actual parsed text.
 * @property errorMessage Error message(s), if any.
 * @property errorDetails Additional error details, if any.
 */
data class ParsedResult(
    @SerializedName("TextOverlay")
    val textOverlay: TextOverlay?,

    @SerializedName("TextOrientation")
    val textOrientation: String?,

    @SerializedName("FileParseExitCode")
    val fileParseExitCode: Int,

    @SerializedName("ParsedText")
    val parsedText: String?,

    @SerializedName("ErrorMessage")
    val errorMessage: Any?,

    @SerializedName("ErrorDetails")
    val errorDetails: Any?
)

/**
 * Represents overlay information for detected text lines.
 *
 * @property lines List of detected lines in the image.
 */
data class TextOverlay(
    @SerializedName("Lines")
    val lines: List<Line>?
)

/**
 * Represents a single line of detected text.
 *
 * @property lineText The text of the line.
 * @property words List of detected words in the line.
 */
data class Line(
    @SerializedName("LineText")
    val lineText: String?,

    @SerializedName("Words")
    val words: List<Word>?
)

/**
 * Represents a single detected word in a line.
 *
 * @property wordText The text of the word.
 * @property left The left position of the word in the image.
 * @property top The top position of the word in the image.
 * @property height The height of the word bounding box.
 * @property width The width of the word bounding box.
 */
data class Word(
    @SerializedName("WordText")
    val wordText: String?,

    @SerializedName("Left")
    val left: Double,

    @SerializedName("Top")
    val top: Double,

    @SerializedName("Height")
    val height: Double,

    @SerializedName("Width")
    val width: Double
)

