<#-- @ftlvariable name="signInAttempt" type="org.springframework.social.connect.web.ProviderSignInAttempt" -->

Продолжив, вы сможете входить в BillionGoods без ввода пароля, при помощи профиля ${signInAttempt.connection.key.providerId} ${signInAttempt.connection.displayName}.

<form action="/account/social/association" method="post">
    <button value="new">
        Я новый покупатель
    </button>

    <button value="registered">
        У меня уже есть логин
    </button>
</form>
