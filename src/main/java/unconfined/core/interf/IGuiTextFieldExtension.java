package unconfined.core.interf;

import java.util.function.Consumer;

public interface IGuiTextFieldExtension {

    void unconfined$addPostRenderTextCallback(Consumer<PostRenderTextArgs> callback);

}
