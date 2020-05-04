package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.botAPIs.OauthChannel;
import zoomapi.botAPIs.OauthMessage;
import zoomapi.utils.Member;
import zoomapi.utils.Message;
import zoomapi.utils.OauthCondition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/************************
 * Singleton Class NewMembersEvent
 ************************/

public class NewMembersEventHandler extends OauthEventHandler {
    private String channelId;
    private String memberId;
    private String fromDate;
    private String toDate;
    private Set<String> memberIds;
    private OauthChannel oauthChannel;
    public NewMembersEventHandler(OauthZoomClient client, String channelId, String fromDate, String toDate) {
        super(client);
        this.oauthChannel = new OauthChannel(client);
        this.channelId = channelId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        memberIds = new HashSet<>();
    }

    private void memberIdsInit() {
        List<Member> members = oauthChannel.listChannelMembers(channelId);
        for(int i = 0; i < members.size(); i++){
            Member member = members.get(i);
            String memberId = member.getId();
            this.memberIds.add(memberId);
        }
    }

    @Override
    public void run() {
        memberIdsInit();
        while (this.work) {
            // System.out.println("checking new member");
            List<Member> members = oauthChannel.listChannelMembers(channelId);
            Set<String> newMemberIds = new HashSet<>();
            for(int i = 0; i < members.size(); i++){
                Member member = members.get(i);
                String memberId = member.getId();
                newMemberIds.add(memberId);
                if(!this.memberIds.contains(memberId)){
                    SubscribeAgency.announce(SubscribeAgency.NOTIFY_NEW_MEMBERS, this.channelId, member);
                }else continue;
            }
            this.memberIds = newMemberIds;
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
