package br.com.tucunare.apoiodigital.agent;

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
