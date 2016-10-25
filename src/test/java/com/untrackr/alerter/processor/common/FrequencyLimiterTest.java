package com.untrackr.alerter.processor.common;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FrequencyLimiterTest {

	@Test
	public void test() {
		long now = 0;
		FrequencyLimiter fl = new FrequencyLimiter(5, 3);
		assertThat(fl.ping(now+1), is(0));
		assertThat(fl.ping(now+2), is(0));
		assertThat(fl.ping(now+3), is(0));
		assertThat(fl.ping(now+4), is(1));
		assertThat(fl.ping(now+5), is(2));
		assertThat(fl.ping(now+9), is(0));
		assertThat(fl.ping(now+10), is(0));
		assertThat(fl.ping(now+20), is(0));
		assertThat(fl.ping(now+30), is(0));
		assertThat(fl.ping(now+31), is(0));
		assertThat(fl.ping(now+32), is(0));
		assertThat(fl.ping(now+33), is(1));
		assertThat(fl.ping(now+34), is(2));
		assertThat(fl.ping(now+37), is(0));
	}

}
