import shared.PdfUtils;
import java.io.File;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // مراقبة مجلد SMS
        watchFolder("C:\\path\\to\\sms\\folder", true);
        
        // مراقبة مجلد WhatsApp
        watchFolder("C:\\path\\to\\whatsapp\\folder", false);
    }

    public static void watchFolder(String path, boolean isSms) throws Exception {
        // إنشاء WatchService لمراقبة المجلدات
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path folder = Paths.get(path);
        folder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        System.out.println("Started watching folder: " + path);

        while (true) {
            WatchKey key = watchService.take(); // انتظار الأحداث
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    File file = new File(folder + "\\" + event.context());
                    if (file.getName().endsWith(".pdf")) {
                        processFile(file, isSms); // معالجة الملف الجديد
                    }
                }
            }
            key.reset();
        }
    }

    public static void processFile(File file, boolean isSms) {
        System.out.println("Processing file: " + file.getPath());
        String text = PdfUtils.extractTextFromPdf(file.getPath());
        String phone = PdfUtils.extractPhoneNumber(text);

        if (text == null || phone == null) {
            System.out.println("Failed to extract text or phone number from: " + file.getName());
            return;
        }

        if (isSms) {
            sendSms(phone, text);
        } else {
            sendViaWhatsApp(phone, file.getPath());
        }
        
        // حذف الملف بعد المعالجة
        if (file.delete()) {
            System.out.println("File deleted successfully: " + file.getName());
        } else {
            System.out.println("Failed to delete file: " + file.getName());
        }
    }

    public static void sendSms(String phone, String message) {
        // طباعة تفاصيل إرسال الرسالة (محاكاة الإرسال عبر مودم GSM)
        System.out.println("Sending SMS to: " + phone);
        System.out.println("Message: " + message);
        // هنا يمكن إضافة كود إرسال الرسائل باستخدام مكتبة GSM
    }

    public static void sendViaWhatsApp(String phone, String pdfPath) {
        // طباعة تفاصيل إرسال الرسالة (محاكاة الإرسال عبر WhatsApp)
        System.out.println("Sending WhatsApp to: " + phone);
        System.out.println("PDF File: " + pdfPath);
        // يمكن إضافة كود أتمتة WhatsApp Web باستخدام مكتبات مثل Selenium
    }
}