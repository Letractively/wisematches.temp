package billiongoods.core.search.descriptive;

import billiongoods.core.Language;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@SearchableBean(uniformityProperty = "pid")
public class MockSearchableEntity {
    @SearchableProperty(column = "account.id", sortable = false)
    private long pid;

    @SearchableProperty(column = "account.nickname")
    private String nickname;

    @SearchableProperty(column = "profile.language")
    private Language language;

    public MockSearchableEntity() {
    }

    public long getPid() {
        return pid;
    }

    public String getNickname() {
        return nickname;
    }

    public Language getLanguage() {
        return language;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
