package src;

public class Main {

    private static final String JSESSIONID = "283AEAE1B7BB8E4C7BEE8C0CE8772B01";
    private static final String BASE_TEXT = "curl 'http://1d3p.wp.codeforces.com/new' \\\n" +
            "  -H 'Connection: keep-alive' \\\n" +
            "  -H 'Cache-Control: max-age=0' \\\n" +
            "  -H 'Upgrade-Insecure-Requests: 1' \\\n" +
            "  -H 'Origin: http://1d3p.wp.codeforces.com' \\\n" +
            "  -H 'Content-Type: application/x-www-form-urlencoded' \\\n" +
            "  -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36' \\\n" +
            "  -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9' \\\n" +
            "  -H 'Referer: http://1d3p.wp.codeforces.com/' \\\n" +
            "  -H 'Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6' \\\n" +
            "  -H 'Cookie: __utma=71512449.1610614168.1599834868.1599834868.1599834868.1; __utmz=71512449.1599834868.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); 70a7c28f3de=z45mu5fp28z2u6rtj3; JSESSIONID=07C30E0461225E3C8B63EC4BEA99F83A' \\\n" +
            "  --data-raw '_af=34be50b38beccce4&proof=4&amount=2&submit=Submit' \\\n" +
            "  --compressed \\\n" +
            "  --insecure";
    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            System.out.println(BASE_TEXT +"  --data-raw \"_af=34be50b38beccce4^&proof=" + i * i + "^&amount=" + i + "^&submit=Submit\" " + "  --insecure ");
        }
    }
}
