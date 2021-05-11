/* Copyright Airship and Contributors */

package com.urbanairship.iam.modal;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.urbanairship.automation.R;
import com.urbanairship.iam.ButtonInfo;
import com.urbanairship.iam.InAppActionUtils;
import com.urbanairship.iam.InAppMessageActivity;
import com.urbanairship.iam.ResolutionInfo;
import com.urbanairship.iam.TextInfo;
import com.urbanairship.iam.view.BackgroundDrawableBuilder;
import com.urbanairship.iam.view.BorderRadius;
import com.urbanairship.iam.view.BoundedLinearLayout;
import com.urbanairship.iam.view.InAppButtonLayout;
import com.urbanairship.iam.view.InAppViewUtils;
import com.urbanairship.iam.view.MediaView;
import com.urbanairship.webkit.AirshipWebChromeClient;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

/**
 * Modal in-app message activity.
 */
public class ModalActivity extends InAppMessageActivity implements InAppButtonLayout.ButtonClickListener {

    private MediaView mediaView;

    @Override
    protected void onCreateMessage(@Nullable Bundle savedInstanceState) {
        if (getMessage() == null) {
            finish();
            return;
        }

        final ModalDisplayContent displayContent = getMessage().getDisplayContent();
        if (displayContent == null) {
            finish();
            return;
        }

        float borderRadius;
        if (displayContent.isFullscreenDisplayAllowed() && getResources().getBoolean(R.bool.ua_iam_modal_allow_fullscreen_display)) {
            borderRadius = 0;
            setTheme(R.style.UrbanAirship_InAppModal_Activity_Fullscreen);
            setContentView(R.layout.ua_iam_modal_fullscreen);
        } else {
            borderRadius = displayContent.getBorderRadius();
            setContentView(R.layout.ua_iam_modal);
        }

        @ModalDisplayContent.Template
        String template = normalizeTemplate(displayContent);

        // Inflate the content before finding other views
        ViewStub content = findViewById(R.id.modal_content);

        content.setLayoutResource(getTemplate(template));
        content.inflate();

        BoundedLinearLayout modal = findViewById(R.id.modal);
        TextView heading = findViewById(R.id.heading);
        TextView body = findViewById(R.id.body);
        InAppButtonLayout buttonLayout = findViewById(R.id.buttons);
        this.mediaView = findViewById(R.id.media);
        Button footer = findViewById(R.id.footer);
        ImageButton dismiss = findViewById(R.id.dismiss);

        // Heading
        if (displayContent.getHeading() != null) {
            InAppViewUtils.applyTextInfo(heading, displayContent.getHeading());

            // Heading overlaps with the dismiss button, so it has more padding on the right than
            // the left. If its centered, normalize the padding to prevent center text being misaligned.
            if (TextInfo.ALIGNMENT_CENTER.equals(displayContent.getHeading().getAlignment())) {
                normalizeHorizontalPadding(heading);
            }

        } else {
            heading.setVisibility(View.GONE);
        }

        // Body
        if (displayContent.getBody() != null) {
            InAppViewUtils.applyTextInfo(body, displayContent.getBody());
        } else {
            body.setVisibility(View.GONE);
        }

        // Media
        if (displayContent.getMedia() != null) {
            mediaView.setChromeClient(new AirshipWebChromeClient(this));
            InAppViewUtils.loadMediaInfo(mediaView, displayContent.getMedia(), getMessageAssets());
        } else {
            mediaView.setVisibility(View.GONE);
        }

        // Button Layout
        if (!displayContent.getButtons().isEmpty()) {
            buttonLayout.setButtons(displayContent.getButtonLayout(), displayContent.getButtons());
            buttonLayout.setButtonClickListener(this);
        } else {
            buttonLayout.setVisibility(View.GONE);
        }

        // Footer
        if (displayContent.getFooter() != null) {
            InAppViewUtils.applyButtonInfo(footer, displayContent.getFooter(), 0);
            footer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull View view) {
                    onButtonClicked(view, displayContent.getFooter());
                }
            });
        } else {
            footer.setVisibility(View.GONE);
        }

        final Drawable background = BackgroundDrawableBuilder.newBuilder(this)
                                                             .setBackgroundColor(displayContent.getBackgroundColor())
                                                             .setBorderRadius(borderRadius, BorderRadius.ALL)
                                                             .build();

        ViewCompat.setBackground(modal, background);

        if (borderRadius > 0) {
            modal.setClipPathBorderRadius(borderRadius);
        }

        // DismissButton
        Drawable dismissDrawable = DrawableCompat.wrap(dismiss.getDrawable()).mutate();
        DrawableCompat.setTint(dismissDrawable, displayContent.getDismissButtonColor());
        dismiss.setImageDrawable(dismissDrawable);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getDisplayHandler() != null) {
                    getDisplayHandler().finished(ResolutionInfo.dismissed(), getDisplayTime());
                }
                finish();
            }
        });
    }

    @Override
    public void onButtonClicked(@NonNull View view, @NonNull ButtonInfo buttonInfo) {
        if (getDisplayHandler() == null) {
            return;
        }

        InAppActionUtils.runActions(buttonInfo);
        getDisplayHandler().finished(ResolutionInfo.buttonPressed(buttonInfo), getDisplayTime());
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mediaView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mediaView.onPause();
    }

    /**
     * Gets the layout for the given template.
     *
     * @param template The modal template.
     * @return The template layout resource ID.
     */
    @LayoutRes
    protected int getTemplate(@NonNull @ModalDisplayContent.Template String template) {
        switch (template) {
            case ModalDisplayContent.TEMPLATE_HEADER_BODY_MEDIA:
                return R.layout.ua_iam_modal_header_body_media;

            case ModalDisplayContent.TEMPLATE_HEADER_MEDIA_BODY:
                return R.layout.ua_iam_modal_header_media_body;

            case ModalDisplayContent.TEMPLATE_MEDIA_HEADER_BODY:
            default:
                return R.layout.ua_iam_modal_media_header_body;
        }
    }

    /**
     * Gets the normalized template from the display content. The template may differ from the
     * display content's template to facilitate theming.
     *
     * @param displayContent The display content.
     * @return The modal template.
     */
    @NonNull
    @ModalDisplayContent.Template
    protected String normalizeTemplate(@NonNull ModalDisplayContent displayContent) {
        String template = displayContent.getTemplate();

        // If we do not have media use TEMPLATE_HEADER_BODY_MEDIA
        if (displayContent.getMedia() == null) {
            return ModalDisplayContent.TEMPLATE_HEADER_BODY_MEDIA;
        }

        // If we do not have a header for template TEMPLATE_HEADER_MEDIA_BODY, but we have media,
        // fallback to TEMPLATE_MEDIA_HEADER_BODY to avoid missing padding at the top modal
        if (template.equals(ModalDisplayContent.TEMPLATE_HEADER_MEDIA_BODY) && displayContent.getHeading() == null && displayContent.getMedia() != null) {
            return ModalDisplayContent.TEMPLATE_MEDIA_HEADER_BODY;
        }

        return template;
    }

    private void normalizeHorizontalPadding(@NonNull TextView view) {
        int padding = Math.max(ViewCompat.getPaddingEnd(view), ViewCompat.getPaddingStart(view));
        view.setPadding(padding, view.getPaddingTop(), padding, view.getPaddingBottom());
        view.requestLayout();
    }
}
