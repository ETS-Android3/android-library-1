/* Copyright Airship and Contributors */

package com.urbanairship.android.layout.model;

import com.urbanairship.android.layout.Thomas;
import com.urbanairship.android.layout.event.Event;
import com.urbanairship.android.layout.event.PagerEvent;
import com.urbanairship.android.layout.property.Border;
import com.urbanairship.android.layout.property.Color;
import com.urbanairship.android.layout.property.ViewType;
import com.urbanairship.json.JsonException;
import com.urbanairship.json.JsonList;
import com.urbanairship.json.JsonMap;
import com.urbanairship.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.urbanairship.android.layout.model.Identifiable.identifierFromJson;

public class PagerModel extends LayoutModel {
    @NonNull
    private final List<PagerModel.Item> items;

    @NonNull
    private final List<BaseModel> children = new ArrayList<>();

    @Nullable
    private final Boolean disableSwipe;

    @Nullable
    private Listener listener;

    private int lastIndex = 0;

    public PagerModel(@NonNull List<PagerModel.Item> items, @Nullable Boolean disableSwipe, @Nullable Color backgroundColor, @Nullable Border border) {
        super(ViewType.PAGER, backgroundColor, border);

        this.items = items;
        this.disableSwipe = disableSwipe;

        for (PagerModel.Item item : items) {
            item.view.addListener(this);
            children.add(item.view);
        }
    }

    @NonNull
    public static PagerModel fromJson(@NonNull JsonMap json) throws JsonException {
        JsonList itemsJson = json.opt("items").optList();
        Boolean disableSwipe = json.opt("disable_swipe").getBoolean();
        Color backgroundColor = backgroundColorFromJson(json);
        Border border = borderFromJson(json);
        List<PagerModel.Item> items = PagerModel.Item.fromJsonList(itemsJson);
        return new PagerModel(items, disableSwipe, backgroundColor, border);
    }

    @Override
    public List<BaseModel> getChildren() {
        return children;
    }

    //
    // Fields
    //

    @NonNull
    public List<PagerModel.Item> getItems() {
        return items;
    }

    @Nullable
    public Boolean getDisableSwipe() {
        return disableSwipe;
    }

    //
    // View Listener
    //

    public interface Listener {
        void onScrollToNext();
        void onScrollToPrevious();
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    //
    // View Actions
    //

    public void onScrollTo(int position, boolean isInternalScroll, long time) {
        Item item = items.get(position);
        String pageId = item.identifier;
        Map<String, JsonValue> pageActions = item.actions;
        String previousPageId = this.items.get(lastIndex).identifier;
        bubbleEvent(new PagerEvent.Scroll(this, position, pageId, pageActions, lastIndex, previousPageId, isInternalScroll, time));
        lastIndex = position;
    }

    public void onConfigured(int position, long time) {
        Item item = items.get(position);
        String pageId = item.identifier;
        Map<String, JsonValue> pageActions = item.actions;
        bubbleEvent(new PagerEvent.Init(this, position, pageId, pageActions, time));
    }

    //
    // Events
    //

    @Override
    public boolean onEvent(@NonNull Event event) {
        return onEvent(event, true);
    }

    private boolean onEvent(@NonNull Event event, boolean bubbleIfUnhandled) {
        switch (event.getType()) {
            case BUTTON_BEHAVIOR_PAGER_NEXT:
                if (listener != null) {
                    listener.onScrollToNext();
                }
                return true;
            case BUTTON_BEHAVIOR_PAGER_PREVIOUS:
                if (listener != null) {
                    listener.onScrollToPrevious();
                }
                return true;
        }

        return bubbleIfUnhandled && super.onEvent(event);
    }

    @Override
    public boolean trickleEvent(@NonNull Event event) {
        if (onEvent(event, false)) {
            return true;
        }

        return super.trickleEvent(event);
    }

    public static class Item {
        @NonNull
        private final BaseModel view;
        @NonNull
        private final String identifier;
        @NonNull
        private final Map<String, JsonValue> actions;

        public Item(@NonNull BaseModel view, @NonNull String identifier, @NonNull Map<String, JsonValue> actions) {
            this.view = view;
            this.identifier = identifier;
            this.actions = actions;
        }

        @NonNull
        public static PagerModel.Item fromJson(@NonNull JsonMap json) throws JsonException {
            JsonMap viewJson = json.opt("view").optMap();
            String identifier = identifierFromJson(json);
            Map<String, JsonValue> actions = json.opt("actions").optMap().getMap();
            BaseModel view = Thomas.model(viewJson);

            return new PagerModel.Item(view, identifier, actions);
        }

        @NonNull
        public static List<PagerModel.Item> fromJsonList(@NonNull JsonList json) throws JsonException {
            List<PagerModel.Item> items = new ArrayList<>(json.size());
            for (int i = 0; i < json.size(); i++) {
                JsonMap itemJson = json.get(i).optMap();
                PagerModel.Item item = PagerModel.Item.fromJson(itemJson);
                items.add(item);
            }
            return items;
        }

        @NonNull
        public BaseModel getView() {
            return view;
        }

        @NonNull
        public String getIdentifier() {
            return identifier;
        }

    }
}
