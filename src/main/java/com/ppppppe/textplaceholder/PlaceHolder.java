package com.ppppppe.textplaceholder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.StringTokenizer;

public class PlaceHolder {
    enum Mode {
        DEFAULT,
        REMOTE
    }

    public String defaultPlaceHolder =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut ut velit nulla. Nunc vulputate eleifend " +
                    "dapibus. Phasellus vestibulum dui quis scelerisque hendrerit. Vestibulum scelerisque consequat " +
                    "viverra. Donec dictum velit eget erat sagittis, vitae vestibulum urna congue. Integer lacus " +
                    "risus, tristique eu diam ac, venenatis fringilla nisi. Aliquam non felis id nulla venenatis " +
                    "tincidunt non nec justo. Sed ut rutrum arcu, et suscipit quam";
    private String placeHolder;
    private Boolean baconIsAvailable;

    public boolean popupIsShown;
    private Integer tokensUsed;

    public PlaceHolder(Mode mode){
        tokensUsed = 0;
        baconIsAvailable = true;
        this.getPlaceHolder(20);
    }

    public @NotNull String generatePlaceholder(int length) {
        StringTokenizer tokenizer = new StringTokenizer(placeHolder, " ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokensUsed; i++) {
            if (tokenizer.hasMoreTokens())
                tokenizer.nextToken();
        }
        while (sb.length() < length) {
            if (!tokenizer.hasMoreTokens()) {
                tokensUsed = 0;
                this.getPlaceHolder(length * 2);
                tokenizer = new StringTokenizer(placeHolder, " ");
            }
            tokensUsed++;
            sb.append(tokenizer.nextToken());
            sb.append(" ");
        }
        return sb.toString();
    }

    public boolean isRemoteAvailable() {
        return baconIsAvailable;
    }

    public @NotNull void getPlaceHolder(int length) {
        try {
            placeHolder = BaconRespond.getSentences(5);
            baconIsAvailable = true;
        } catch (IOException e) {
            if (baconIsAvailable)
                popupIsShown = false;
            placeHolder = defaultPlaceHolder;
            baconIsAvailable = false;
        }
    }
}
