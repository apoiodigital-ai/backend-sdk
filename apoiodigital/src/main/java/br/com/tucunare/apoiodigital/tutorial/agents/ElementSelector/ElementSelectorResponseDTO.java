package br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector;

/**
 * @param viewId   The {@code viewId} string of the chosen element, copied verbatim from the
 *                 request's element list (see the element-selector rule's output format) —
 *                 the SDK matches it back against its own captured elements to position the
 *                 spotlight.
 * @param precisao Model's own confidence (0.0-1.0) in the chosen viewId, surfaced to the SDK
 *                 as-is so the partner app can decide whether to trust the highlight or fall
 *                 back to something safer (e.g. asking again) when confidence is low.
 */
public record ElementSelectorResponseDTO(String viewId, String raciocinio, Double precisao) {
}
