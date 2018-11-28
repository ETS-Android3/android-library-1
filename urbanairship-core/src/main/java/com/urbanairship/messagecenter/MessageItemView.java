/* Copyright 2018 Urban Airship and Contributors */

package com.urbanairship.messagecenter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.urbanairship.R;
import com.urbanairship.richpush.RichPushMessage;
import com.urbanairship.util.ViewUtils;

/**
 * Message Center item view.
 */
public class MessageItemView extends FrameLayout {

    private static final int[] STATE_HIGHLIGHTED = { R.attr.ua_state_highlighted };

    private TextView titleView;
    private TextView dateView;
    private ImageView iconView;
    private CheckBox checkBox;

    private boolean isHighlighted;
    private OnClickListener selectionListener;

    private Typeface titleTypeface;
    private Typeface titleReadTypeface;


    public MessageItemView(@NonNull Context context) {
        this(context, null, R.attr.messageCenterStyle);
    }

    public MessageItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.messageCenterStyle);
    }

    public MessageItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.MessageCenter);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MessageItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initializes the view.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     * @param defStyleRes A resource identifier of a style resource that
     * supplies default values for the view, used only if
     * defStyleAttr is 0 or cannot be found in the theme. Can be 0
     * to not look for defaults.
     */
    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int contentLayout = R.layout.ua_item_mc_content;
        int dateTextAppearance;
        int titleTextAppearance;

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageCenter, defStyleAttr, defStyleRes);

        if (attributes.getBoolean(R.styleable.MessageCenter_messageCenterItemIconEnabled, false)) {
            contentLayout = R.layout.ua_item_mc_icon_content;
        }

        dateTextAppearance = attributes.getResourceId(R.styleable.MessageCenter_messageCenterItemDateTextAppearance, -1);

        titleTextAppearance = attributes.getResourceId(R.styleable.MessageCenter_messageCenterItemTitleTextAppearance, -1);

        int background = attributes.getResourceId(R.styleable.MessageCenter_messageCenterItemBackground, -1);
        if (background > 0) {
            setBackgroundResource(background);
        }

        attributes.recycle();

        View contentView = View.inflate(context, contentLayout, this);

        titleView = contentView.findViewById(R.id.title);
        ViewUtils.applyTextStyle(context, titleView, titleTextAppearance);
        if (titleView.getTypeface() != null) {
            titleReadTypeface = titleView.getTypeface();
            int style = titleView.getTypeface().getStyle();
            style |= Typeface.BOLD;
            titleTypeface = Typeface.create(titleView.getTypeface(), style);
        } else {
            titleReadTypeface = Typeface.DEFAULT;
            titleTypeface = Typeface.DEFAULT_BOLD;
        }

        dateView = contentView.findViewById(R.id.date);
        ViewUtils.applyTextStyle(context, dateView, dateTextAppearance);

        iconView = contentView.findViewById(R.id.image);
        if (iconView != null) {
            iconView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionListener != null) {
                        selectionListener.onClick(MessageItemView.this);
                    }
                }
            });
        }

        checkBox = contentView.findViewById(R.id.checkbox);
        if (checkBox != null) {
            checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectionListener != null) {
                        selectionListener.onClick(MessageItemView.this);
                    }
                }
            });
        }

    }

    /**
     * Updates the view's message.
     *
     * @param message The message.
     * @param placeholder Image place holder.
     */
    void updateMessage(@NonNull RichPushMessage message, @DrawableRes int placeholder) {
        titleView.setText(message.getTitle());
        dateView.setText(DateFormat.getDateFormat(getContext()).format(message.getSentDate()));

        if (message.isRead()) {
            titleView.setTypeface(titleReadTypeface);
        } else {
            titleView.setTypeface(titleTypeface);
        }

        if (checkBox != null) {
            checkBox.setChecked(isActivated());
        }

        if (iconView != null) {
            ImageLoader.shared(getContext()).load(message.getListIconUrl(), placeholder, iconView);
        }
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        if (checkBox != null) {
            checkBox.setChecked(activated);
        }
    }

    /**
     * Sets the highlight state item.
     *
     * @param isHighlighted {@code true} to highlight the view, otherwise {@code false}.
     */
    void setHighlighted(boolean isHighlighted) {
        if (this.isHighlighted != isHighlighted) {
            this.isHighlighted = isHighlighted;
            refreshDrawableState();
        }
    }

    /**
     * Sets the selection listener when the item's icon or checkbox is tapped.
     *
     * @param listener A click listener.
     */
    void setSelectionListener(@Nullable View.OnClickListener listener) {
        this.selectionListener = listener;
    }

    @SuppressLint("UnknownNullness")
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if (isHighlighted) {
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState, STATE_HIGHLIGHTED);
            return drawableState;
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }


}
