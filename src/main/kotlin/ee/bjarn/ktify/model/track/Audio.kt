package ee.bjarn.ktify.model.track

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioFeatures(
    val acousticness: Float,
    @SerialName("analysis_url")
    val analysisUrl: String,
    val danceability: Float,
    @SerialName("duration_ms")
    val durationMs: Int,
    val energy: Float,
    val id: String,
    val instrumentalness: Float,
    val key: Int,
    val liveness: Float,
    val loudness: Float,
    val mode: Int,
    val speechiness: Float,
    val tempo: Float,
    @SerialName("time_signature")
    val timeSignature: Int,
    @SerialName("track_href")
    val trackHref: String,
    val type: String,
    val url: String,
    val valence: Float
)

@Serializable
data class AudioAnalysis(
    val meta: AudioAnalysisMeta,
    val track: AudioAnalysisTrack,
    val bars: List<AudioAnalysisBar>,
    val beats: List<AudioAnalysisBeat>,
    val sections: List<AudioAnalysisSection>,
    val segments: List<AudioAnalysisSegment>,
    val tatums: List<AudioAnalysisTatum>,
)

@Serializable
data class AudioAnalysisMeta(
    @SerialName("analyzer_version")
    val analyzerVersion: String,
    val platform: String,
    @SerialName("detailed_status")
    val detailedStatus: String,
    @SerialName("status_code")
    val statusCode: Int,
    val timestamp: Int,
    @SerialName("analysis_time")
    val analysisTime: Double,
    @SerialName("input_process")
    val inputProcess: String,
)

@Serializable
data class AudioAnalysisTrack(
    @SerialName("num_samples")
    val numSamples: Int,
    val duration: Double,
    @SerialName("sample_md5")
    val sampleMd5: String = "",
    @SerialName("offset_seconds")
    val offsetSeconds: Int,
    @SerialName("window_seconds")
    val windowSeconds: Int,
    @SerialName("analysis_sample_rate")
    val analysisSampleRate: Int,
    @SerialName("analysis_channel")
    val analysisChannel: Int,
    @SerialName("end_of_fade_in")
    val endOfFaseIn: Int,
    @SerialName("start_of_fade_out")
    val startOfFadeOut: Int,
    val loudness: Float,
    val tempo: Float,
    @SerialName("tempo_confidence")
    val tempoConfidence: Double,
    @SerialName("time_signature")
    val timeSignature: Int,
    @SerialName("time_signature_confidence")
    val timeSignatureConfidence: Double,
    val key: Int,
    @SerialName("key_confidence")
    val keyConfidence: Double,
    val mode: Int,
    @SerialName("mode_confidence")
    val modeConfidence: Double,
    val codestring: String,
    @SerialName("code_version")
    val codeVersion: Double,
    val echoprintstring: String,
    @SerialName("echoprint_version")
    val echoprintVersion: Double,
    val synchstring: String,
    @SerialName("synch_version")
    val synchVersion: Double,
    val rhythmstring: String,
    @SerialName("rhythm_version")
    val rhythmVersion: Double,
)

@Serializable
data class AudioAnalysisBar(
    val start: Double,
    val duration: Double,
    val confidence: Double,
)

@Serializable
data class AudioAnalysisBeat(
    val start: Double,
    val duration: Double,
    val confidence: Double,
)

@Serializable
data class AudioAnalysisSection(
    val start: Double,
    val duration: Double,
    val confidence: Double,
    val loudness: Double,
    val tempo: Double,
    @SerialName("tempo_confidence")
    val tempoConfidence: Double,
    val key: Int,
    @SerialName("key_confidence")
    val keyConfidence: Double,
    val mode: Double,
    @SerialName("mode_confidence")
    val modeConfidence: Double,
    @SerialName("time_signature")
    val timeSignature: Int,
    @SerialName("time_signature_confidence")
    val timeSignatureConfidence: Double,
)

@Serializable
data class AudioAnalysisSegment(
    val start: Double,
    val duration: Double,
    val confidence: Double,
    @SerialName("loudness_start")
    val loudnessStart: Double,
    @SerialName("loudness_max")
    val loudnessMax: Double,
    @SerialName("loudness_max_time")
    val loudnessMaxTime: Double,
    @SerialName("loudness_end")
    val loudnessEnd: Double,
    val pitches: List<Double>,
    val timbre: List<Double>,
)

@Serializable
data class AudioAnalysisTatum(
    val start: Double,
    val duration: Double,
    val confidence: Double,
)
