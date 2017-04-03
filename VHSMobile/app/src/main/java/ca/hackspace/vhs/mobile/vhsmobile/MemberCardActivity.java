package ca.hackspace.vhs.mobile.vhsmobile;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ca.hackspace.vhs.mobile.nomos.Nomos;

public class MemberCardActivity extends AppCompatActivity {

    private String currentTag;

    protected void setCurrentTag(Tag tag) {
        this.setCurrentTag(bytesToHex(tag.getId()));
    }

    protected void setCurrentTag(String value) {
        this.currentTag = value;
    }

    protected String getCurrentTag() {
        return this.currentTag;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3 - 1];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];

            if ((j * 3 + 2) < hexChars.length)
                hexChars[j * 3 + 2] = ':';
        }
        return new String(hexChars);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setCurrentTag((Tag) extras.get("tag"));
        }

        if (getCurrentTag() != null) {
            Nomos.Services().ValidateGenuineCard(getCurrentTag(), new Nomos.IBoolResultHandler() {
                @Override
                public void handle(boolean value) {
                    TextView cardKey = (TextView) MemberCardActivity.this.findViewById(R.id.cardKey);

                    String genuine = "Not Genuine";

                    if (value) genuine = "Genuine!";

                    cardKey.setText(getCurrentTag() + " - " + genuine);
                }

                @Override
                public void error(Exception ex) {

                }
            });
        }
    }
}
