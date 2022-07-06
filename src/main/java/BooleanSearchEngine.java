import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> storage = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        for (File item : Objects.requireNonNull(pdfsDir.listFiles())) {
            var doc = new PdfDocument(new PdfReader(item));
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
                }
                for (Map.Entry<String, Integer> freq : freqs.entrySet()) {
                    List<PageEntry> pageEntries = new ArrayList<>();
                    PageEntry pageEntry = new PageEntry(item.getName(), i, freq.getValue());
                    if (!storage.containsKey(freq.getKey())) {
                        pageEntries.add(pageEntry);
                        storage.put(freq.getKey(), pageEntries);
                    } else {
                        pageEntries = storage.get(freq.getKey());
                        pageEntries.add(pageEntry);
                        Collections.sort(pageEntries);
                        storage.put(freq.getKey(), pageEntries);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        for (Map.Entry<String, List<PageEntry>> request : storage.entrySet()) {
            if (word.equals(request.getKey())) {
                return request.getValue();
            }
        }
        return Collections.emptyList();
    }
}
