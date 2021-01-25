/**
 * External dependencies
 */
import * as React from 'react';
import { __ } from '@wordpress/i18n';
import { ArrowButton } from '@automattic/onboarding';

/**
 * Internal dependencies
 */

interface Props {
	onClick: () => void;
}

const UseYourDomainItem: React.FunctionComponent< Props > = ( { onClick } ) => {
	return (
		/* eslint-disable jsx-a11y/click-events-have-key-events */
		/* eslint-disable jsx-a11y/interactive-supports-focus */
		<div role="button" className="domain-picker__suggestion-item type-link" onClick={ onClick }>
			<div className="domain-picker__suggestion-item-name">
				<span className="domain-picker__domain-wrapper with-margin with-bold-text">
					{ __( 'Already own a domain?', __i18n_text_domain__ ) }
				</span>
				<div>
					<span className="domain-picker__item-tip">
						{ __( "You can use it as your site's address", __i18n_text_domain__ ) }
					</span>
				</div>
			</div>
			<ArrowButton arrow="right" onClick={ onClick }>
				{ __( 'Use a domain I own', __i18n_text_domain__ ) }
			</ArrowButton>
		</div>
	);
};

export default UseYourDomainItem;
