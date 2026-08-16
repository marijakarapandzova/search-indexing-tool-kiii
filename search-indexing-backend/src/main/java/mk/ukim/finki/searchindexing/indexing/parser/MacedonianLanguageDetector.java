package mk.ukim.finki.searchindexing.indexing.parser;

import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * A dependency-free heuristic detector: Macedonian is written in Cyrillic, so
 * the base signal is "how much of the text is Cyrillic letters". That alone
 * cannot distinguish Macedonian from closely related languages that share the
 * Cyrillic alphabet (Bulgarian, Serbian, Russian), so the score is adjusted by
 * two further signals:
 *
 * <ul>
 *   <li>Letters that exist in the Macedonian alphabet but not in Bulgarian
 *       (ѓ, ќ, ѕ, џ, љ, њ) are a strong positive signal.</li>
 *   <li>Letters that do not exist in the Macedonian alphabet at all (Bulgarian
 *       ъ/ю/я, Russian/Ukrainian ы/э/ё/і/ї, Serbian ђ/ћ) are a negative
 *       signal.</li>
 * </ul>
 */
@Component
public class MacedonianLanguageDetector implements LanguageDetector {
    private static final Set<Character> MACEDONIAN_SPECIFIC_LETTERS = Set.of(
        'ѓ', 'Ѓ', 'ќ', 'Ќ', 'ѕ', 'Ѕ', 'џ', 'Џ', 'љ', 'Љ', 'њ', 'Њ'
    );

    private static final Set<Character> NON_MACEDONIAN_CYRILLIC_LETTERS = Set.of(
        'ъ', 'Ъ', 'ю', 'Ю', 'я', 'Я', 'ы', 'Ы', 'э', 'Э', 'ё', 'Ё',
        'і', 'І', 'ї', 'Ї', 'ў', 'Ў', 'ђ', 'Ђ', 'ћ', 'Ћ'
    );

    @Override
    public double macedonianConfidence(String text) {
        if (text == null || text.isBlank()) {
            return 0.0;
        }

        int letters = 0;
        int cyrillicLetters = 0;
        int macedonianSpecificLetters = 0;
        int nonMacedonianCyrillicLetters = 0;

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (!Character.isLetter(character)) {
                continue;
            }
            letters++;
            Character.UnicodeBlock block = Character.UnicodeBlock.of(character);
            boolean isCyrillic = block == Character.UnicodeBlock.CYRILLIC
                || block == Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY;
            if (isCyrillic) {
                cyrillicLetters++;
            }
            if (MACEDONIAN_SPECIFIC_LETTERS.contains(character)) {
                macedonianSpecificLetters++;
            }
            if (NON_MACEDONIAN_CYRILLIC_LETTERS.contains(character)) {
                nonMacedonianCyrillicLetters++;
            }
        }

        if (letters == 0) {
            return 0.0;
        }

        double cyrillicRatio = (double) cyrillicLetters / letters;
        double macedonianSpecificRatio = (double) macedonianSpecificLetters / letters;
        double nonMacedonianRatio = (double) nonMacedonianCyrillicLetters / letters;

        // Base score from how "Cyrillic" the text is, a bonus from letters unique to
        // Macedonian (saturating quickly - a handful is already a strong signal), and
        // a penalty for letters that do not belong to the Macedonian alphabet at all.
        double score = 0.7 * cyrillicRatio
            + Math.min(0.3, macedonianSpecificRatio * 15)
            - Math.min(0.6, nonMacedonianRatio * 10);

        return Math.max(0.0, Math.min(1.0, score));
    }
}
