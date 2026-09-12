package br.com.tucunare.apoiodigital.agent;

/**
 * One element of the UI tree captured by the SDK on the partner's screen ("elementos" in every
 * request body). Field names and types mirror the SDK's {@code CapturedElement} exactly
 * (frontend-sdk, src/types.ts) — this record IS the wire contract, so renaming a component
 * here breaks every SDK install in the field.
 *
 * <p>{@code viewId} is the SDK-resolved stable identifier (native resource id,
 * contentDescription/accessibilityLabel, or a synthetic per-scan fallback) — a string, echoed
 * back verbatim in {@code AcharRespostaResponseDTO.viewID} so the SDK can find the element
 * again in its own index. {@code text} is guaranteed "" when {@code isSecure} is true (masked
 * in native code before serialization). Coordinates are absolute in the device's screen space;
 * they are given to the agents as context but deliberately excluded from the Componente
 * assinatura hash, which must stay stable across device sizes.</p>
 */
public record CapturedElementDTO(
        String viewId,
        String className,
        String text,
        Boolean isSecure,
        Boolean isInteractive,
        Double x,
        Double y,
        Double width,
        Double height
) {
}
