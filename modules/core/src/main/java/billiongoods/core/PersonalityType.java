package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum PersonalityType {
    MEMBER,
    VISITOR;

    private PersonalityType() {
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isVisitor() {
        return this == VISITOR;
    }
}