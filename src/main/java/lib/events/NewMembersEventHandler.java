package lib.events;

import lib.botFacing.clients.OauthZoomClient;
import lib.botFacing.oauth.OauthChannel;
import lib.cache.databaseData.ChannelMember;
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
        List<ChannelMember> members = oauthChannel.listChannelMembers(channelId);
        for(int i = 0; i < members.size(); i++){
            ChannelMember member = members.get(i);
            String memberId = member.getMemberId();
            this.memberIds.add(memberId);
        }
        System.out.println("New members listener starts working...");
    }

    @Override
    public void run() {
        memberIdsInit();
        while (this.work) {
            // System.out.println("checking new member");
            List<ChannelMember> members = oauthChannel.listChannelMembers(channelId);
            Set<String> newMemberIds = new HashSet<>();
            for(int i = 0; i < members.size(); i++){
                ChannelMember member = members.get(i);
                String memberId = member.getMemberId();
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
