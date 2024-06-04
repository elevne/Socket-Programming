import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSResolver {

    private static final String DNS_SERVER_IP = "8.8.8.8";

    public static String requestDNS(String domain) {
        try {
            byte[] query = buildDNSQuery(domain);
            DatagramSocket socket = new DatagramSocket();
            InetAddress dnsServer = InetAddress.getByName(DNS_SERVER_IP);
            // Send the DNS query
            DatagramPacket packet = new DatagramPacket(query, query.length, dnsServer, 53);
            socket.send(packet);

            // Receive the DNS response
            byte[] response = new byte[512];
            packet = new DatagramPacket(response, response.length);
            socket.receive(packet);
            return parseDNSResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("NO IP ADDR FOUND");
    }

    public static void main(String[] args) {
        String domain = "ec2-52-79-217-87.ap-northeast-2.compute.amazonaws.com";
        try {
            byte[] query = buildDNSQuery(domain);
            DatagramSocket socket = new DatagramSocket();
            InetAddress dnsServer = InetAddress.getByName(DNS_SERVER_IP);
            // Send the DNS query
            DatagramPacket packet = new DatagramPacket(query, query.length, dnsServer, 53);
            socket.send(packet);

            // Receive the DNS response
            byte[] response = new byte[512];
            packet = new DatagramPacket(response, response.length);
            socket.receive(packet);

            // Parse and print the response
            parseDNSResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] buildDNSQuery(String domain) throws Exception {
        byte[] header = {
                (byte) 0xAA, (byte) 0xAA, // ID
                (byte) 0x01, (byte) 0x00, // Flags (standard query)
                (byte) 0x00, (byte) 0x01, // QDCOUNT (1 question)
                (byte) 0x00, (byte) 0x00, // ANCOUNT (0 answers)
                (byte) 0x00, (byte) 0x00, // NSCOUNT (0 authority records)
                (byte) 0x00, (byte) 0x00  // ARCOUNT (0 additional records)
        };

        String[] domainParts = domain.split("\\.");
        byte[] question = new byte[domain.length() + 2 + 4];
        int pos = 0;
        for (String part : domainParts) {
            question[pos++] = (byte) part.length();
            for (char c : part.toCharArray()) {
                question[pos++] = (byte) c;
            }
        }
        question[pos++] = 0; // End of domain name

        question[pos++] = 0; // Type A
        question[pos++] = 1;
        question[pos++] = 0; // Class IN
        question[pos++] = 1;

        byte[] query = new byte[header.length + question.length];
        System.arraycopy(header, 0, query, 0, header.length);
        System.arraycopy(question, 0, query, header.length, question.length);

        return query;
    }

    private static String parseDNSResponse(byte[] response) throws Exception {
        int pos = 12; // Skip header
        while (response[pos] != 0) { // Skip question
            pos++;
        }
        pos += 5; // Skip null byte and QTYPE/QCLASS
        int ancount = ((response[6] & 0xFF) << 8) | (response[7] & 0xFF);
        if (ancount == 0) {
            throw new RuntimeException("NO IP ADDR FOUND");
        }
        while (ancount-- > 0) {
            pos += 2; // Skip name pointer
            int type = ((response[pos] & 0xFF) << 8) | (response[pos + 1] & 0xFF);
            pos += 8; // Skip TYPE, CLASS, TTL
            int rdlength = ((response[pos] & 0xFF) << 8) | (response[pos + 1] & 0xFF);
            pos += 2;
            if (type == 1) { // Type A
                byte[] ip = new byte[rdlength];
                System.arraycopy(response, pos, ip, 0, rdlength);
                return (ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." + (ip[2] & 0xFF) + "." + (ip[3] & 0xFF);
            }
            pos += rdlength;
        }
        throw new RuntimeException("NO IP ADDR FOUND");
    }
}
