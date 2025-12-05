package br.com.alura.screenmatch.service;
import com.google.genai.Client;
import com.google.genai.types.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConsultaGemini {

    private static final String KEY = System.getenv("GeminiAPI_KEY");

    public static String traduzir(String prompt) {
        Client client = Client.builder()
                .apiKey(KEY)
                .build();


        GenerationConfig config = GenerationConfig.builder()
                .maxOutputTokens(10)
                .build();

        Content content = Content.fromParts(
                Part.fromText(prompt),
                Part.fromText("Traduza para o português com no máximo 255 palavras.")
        );

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        content,
                        null);

        return response.text();
    }

    public static String analisarImagem(String prompt, String imagemUrl) throws IOException {
        Client client = Client.builder()
                .apiKey(KEY)
                .build();

        byte[] imageBytes = Files.readAllBytes(Paths.get(imagemUrl));
        String mime = "image/jpeg";

        Part image = Part.fromBytes(imageBytes, mime);

        Content content = Content.fromParts(
                Part.fromText(prompt),
                image
        );

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        content,
                        null);

        return response.text();
    }
}