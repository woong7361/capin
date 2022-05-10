package com.hanghae.finalp;

import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class FinalPApplicationTests {

    @Autowired private EntityManager em;

    public void clearContext() {
        em.flush();
        em.clear();
    }

    @Test
    @Rollback(value = false)
    public void testMember() throws Exception{
        //given
        Member member = Member.createMember("kakaoId", "username", null, null);
        //when
        em.persist(member);
        clearContext();
        Member findMember = em.find(Member.class, member.getId());
        //then

        assertThat(member.getKakaoId()).isEqualTo(findMember.getKakaoId());
        assertThat(member.getKakaoId()).isEqualTo("kakaoId");
    }

    @Test
    @Rollback(value = false)
    public void groupTest() throws Exception{
        //given
        Member member = Member.createMember("kakaoId", "username", null, null);
        Group group =
                Group.createGroup("title", "desc", 5, "adrress", null, null, member);
        em.persist(member);
        em.persist(group);
        clearContext();
        //when
        Group findGroup = em.find(Group.class, group.getId());
        //then

        assertThat(findGroup.getGroupTitle()).isEqualTo("title");
        assertThat(findGroup.getMemberGroups().get(0).getAuthority()).isEqualTo(Authority.OWNER);
        assertThat(findGroup.getMemberGroups().get(0).getChatroom().getChatroomTitle()).isEqualTo("title");
    }

    @Test
    @Rollback(value = false)
    public void chatMember() throws Exception{
        //given
        Member member1 = Member.createMember("kakaoId1", "username1", null, null);
        Member member2 = Member.createMember("kakaoId2", "username2", null, null);
        Chatroom chatroom = Chatroom.createChatroomByMember("titleMember", member1, member2);
        em.persist(member1);
        em.persist(member2);
        em.persist(chatroom);
        clearContext();
        //when
        Chatroom findChatroom = em.find(Chatroom.class, chatroom.getId());
        //then

        assertThat(findChatroom.getChatroomTitle()).isEqualTo("titleMember");
        assertThat(findChatroom.getChatMembers().size()).isEqualTo(2);

    }

    @Test
    public void test() throws Exception{
        //given
        Long a = 3L;
        System.out.println("a.toString() = " + a.toString());
        //when

        //then
    }

}