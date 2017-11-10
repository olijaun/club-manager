package org.jaun.clubmanager.domain.model;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserApplicationServiceBeanTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserApplicationServiceBean userApplicationServiceBean;

    /**
     * This test takes about ab minute on my machine. EJBContainer seems to be pretty useless to me. But it's here to show that is possible...
     */
    @Ignore
    @Test
    public void getUser_ejbContainer() throws Exception {
        // given
        EJBContainer ejbContainer = EJBContainer.createEJBContainer();

        Context context = ejbContainer.getContext();
        UserApplicationServiceBean userApplicationServiceBean = (UserApplicationServiceBean) context.lookup("java:global/classes/UserApplicationServiceBean");

        // when
        User user = userApplicationServiceBean.getUser(new UserId("abc"));

        // then
        assertNotNull(user);
    }

    @Test
    public void getUser() throws Exception {
        // given
        User user = new User(new UserId("abc"), "name");
        when(userRepository.getUser(any(UserId.class))).thenReturn(user);

        // when
        User returnedUser = userApplicationServiceBean.getUser(new UserId("abc"));

        // then
        assertThat(returnedUser, equalTo(user));
        verify(userRepository).getUser(eq(user.getId()));
    }
}