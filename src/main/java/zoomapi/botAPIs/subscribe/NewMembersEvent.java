package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.utils.Member;
import zoomapi.utils.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/************************
 * Singleton Class NewMembersEvent
 ************************/

public class NewMembersEvent extends OauthEvent {
    private String channelId;
    private String memberId;
    private String fromDate;
    private String toDate;
    private Set<String> memberIds;

    public NewMembersEvent(OauthZoomClient client, String channelId, String fromDate, String toDate) {
        super(client);
        this.channelId = channelId;
        this.fromDate = fromDate;
        this.toDate = toDate;
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
            System.out.println("checking new member");
            List<Member> members = oauthChannel.listChannelMembers(channelId);
            for(int i = 0; i < members.size(); i++){
                Member member = members.get(i);
                String memberId = member.getId();
                if(!this.memberIds.contains(memberId)){
                    this.memberIds.add(memberId);
                    SubscribeAgency.announce(SubscribeAgency.NOTIFY_NEW_MEMBERS, this.channelId, memberId);
                }else continue;
            }
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
