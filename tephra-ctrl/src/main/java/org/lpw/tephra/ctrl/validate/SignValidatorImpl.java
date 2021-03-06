package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.context.Header;
import org.lpw.tephra.ctrl.security.TrustfulIp;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(Validators.SIGN)
public class SignValidatorImpl extends ValidatorSupport implements SignValidator {
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    @Override
    public void setSignEnable(boolean enable) {
        threadLocal.set(enable);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !enable() || trustfulIp.contains(header.getIp()) || request.checkSign();
    }

    private boolean enable() {
        return threadLocal.get() == null || threadLocal.get();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 9995;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-sign";
    }
}
